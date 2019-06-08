package io.gcat.parser;

import io.gcat.entity.GCInfo;
import io.gcat.entity.JVMParameter;
import io.gcat.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMSParNewParser implements Parser {

    private static final Logger logger = LoggerFactory.getLogger(CMSParNewParser.class);

    private static Pattern changePattern = Pattern.compile("(\\d+)K->(\\d+)K\\((\\d+)K\\), ([0-9.]+) secs");

    private static Pattern gcTimePattern = Pattern.compile("real=([0-9.]+) secs");
    /**
     * 1.6, 1.7, 1.8...
     **/
    private String jvmVersion;

    private JVMParameter jvmParameter;

    private long lineCount = 0;

    private boolean CMS;

    private long CMSStartTimestamp;

    private List<GCInfo> list = new LinkedList<>();

    public CMSParNewParser(String jvmVersion, JVMParameter jvmParameter) {
        this.jvmVersion = jvmVersion;
        this.jvmParameter = jvmParameter;
    }

    @Override
    public void feed(String line) {
        lineCount++;
        try {
            parseLineInternal(line);
        } catch (LineParseException e) {
            String message = "not parseable line#{}:{}";
            int end = line.length() < 30 ? line.length() : 30;
            String shortLine = line.substring(0, end);
            logger.warn(message, lineCount, shortLine);
        }
    }

    private void parseLineInternal(String line) throws LineParseException {
        LineParser parser = LineParser.INSTANCE.reset(line);
        parser.parseTimestamp();
        parser.parseBootTime();
        if (parser.restStartWith("[GC (Allocation Failure) ")) {
            parser.parseYoungGeneration();
            parser.parseHeap();
            parser.parseGcPause();
            GCInfo gcInfo = parser.getGCInfo();
            gcInfo.setType(GCInfo.GCType.ParNew);
            list.add(gcInfo);
        } else if (parser.restStartWith("[GC (CMS Initial Mark)")) {
            CMS = true;
            CMSStartTimestamp = parser.getTimestamp();
        } else if (parser.restStartWith("[CMS-concurrent-reset:")) {
            CMS = false;
            GCInfo gcInfo = parser.getGCInfo();
            gcInfo.setType(GCInfo.GCType.CMS);
            gcInfo.setGcPause(gcInfo.getTimestamp() - CMSStartTimestamp);
            list.add(gcInfo);
        }
    }

    @Override
    public String query(String sql) {
//        write(new File("/tmp/gcat.out"));
        Iterator<GCInfo> it = list.iterator();
        GCInfo first = it.next();
        long firstTimestamp = first.getTimestamp();
        long lastTimestamp = firstTimestamp;
        long maxPause = first.getGcPause();
        long maxPauseTimestamp = 0;
        long pauseSum = maxPause;
        long intervalSum = 0;
        long minInterval = Long.MAX_VALUE;
        long minIntervalTimestamp = 0;
        int count = 1;
        while (it.hasNext()) {
            GCInfo r = it.next();
            long t = r.getTimestamp();
            long gcTime = r.getGcPause();
            pauseSum += gcTime;
            if (maxPause < gcTime) {
                maxPause = gcTime;
                maxPauseTimestamp = t;
            }
            long interval = t - lastTimestamp;
            if (interval < minInterval) {
                minInterval = interval;
                minIntervalTimestamp = t;
            }
            intervalSum += interval;
            lastTimestamp = t;
            count++;
        }

        Summary heapSummary = new Summary()
                .setName("Heap")
                .setCount(count)
                .setDuration(Duration.ofMillis(lastTimestamp - firstTimestamp))
                .setAvgPause(pauseSum / count)
                .setMaxPause(maxPause)
                .setMaxPauseTimestamp(maxPauseTimestamp)
                .setAvgInterval(intervalSum / (count - 1))
                .setMinInterval(minInterval)
                .setMinIntervalTimestamp(minIntervalTimestamp);

        System.out.println(heapSummary);
        return null;
    }

    public void write(File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (GCInfo r : list) {
                fileWriter.write(r.toString());
                fileWriter.write("\n");
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    private enum LineParser {

        INSTANCE;

        int cursor;

        private String line;

        private GCInfo gcInfo = new GCInfo();

        private void parseTimestamp() throws LineParseException {
            int s = "2019-05-05T15:52:07.063+0800".length();
            try {
                long timestamp = DateUtil.parse(line.substring(0, s));
                gcInfo.setTimestamp(timestamp);
                cursor = s + 2; //skip ": "
            } catch (Exception e) {
                throw new LineParseException();
            }
        }

        private void parseBootTime() {
            int s = cursor;
            int e = line.indexOf(": ", s);
            //parse timestamp
            Float bootTime = Float.valueOf(line.substring(s, e));
            gcInfo.setBootTime((long) (bootTime * 1000));

            cursor = e + 2; //skip ": "
        }

        private void parseYoungGeneration() {
            int s = cursor;
            s = line.indexOf("[ParNew: ", s) + "[ParNew: ".length();
            Matcher m = changePattern.matcher(line.substring(s, line.length()));
            if (!m.find()) {
                throw new IllegalArgumentException("not found new region change.");
            }
            Integer regionUsedBefore = Integer.valueOf(m.group(1));
            Integer regionUsedAfter = Integer.valueOf(m.group(2));
            Integer regionSize = Integer.valueOf(m.group(3));
            gcInfo.setYoungUsedBefore(regionUsedBefore);
            gcInfo.setYoungUsedAfter(regionUsedAfter);
            gcInfo.setYoungSize(regionSize);

            cursor = s + m.group().length() + 2; //skip "] "
        }

        private void parseHeap() {
            int s = cursor;
            Matcher m = changePattern.matcher(line.substring(s, line.length()));
            if (!m.find()) {
                throw new IllegalArgumentException("not found heap change.");
            }
            Integer heapUsedBefore = Integer.valueOf(m.group(1));
            Integer heapUsedAfter = Integer.valueOf(m.group(2));
            Integer heapSize = Integer.valueOf(m.group(3));

            gcInfo.setHeapUsedBefore(heapUsedBefore);
            gcInfo.setHeapUsedAfter(heapUsedAfter);
            gcInfo.setHeapSize(heapSize);

            cursor = s + m.group().length() + 2; //skip "] "
        }

        private void parseGcPause() {
            int s = cursor;
            Matcher m = gcTimePattern.matcher(line.substring(s, line.length()));
            if (m.find()) {
                Float gcTime = Float.valueOf(m.group(1));
                gcInfo.setGcPause((long) (gcTime * 1000));
            } else {
                throw new IllegalStateException("not found real time.");
            }
        }

        private boolean restStartWith(String prefix) {
            return line.substring(cursor).startsWith(prefix);
        }

        public LineParser reset(String line) {
            this.line = line;
            this.cursor = 0;
            this.gcInfo = new GCInfo();
            return this;
        }

        public GCInfo getGCInfo() {
            return gcInfo.copy();
        }

        public long getTimestamp() {
            return gcInfo.getTimestamp();
        }

        public long getBootTime() {
            return gcInfo.getBootTime();
        }
    }
}

package io.gcat.parser;

import io.gcat.entity.GCInfo;
import io.gcat.entity.JVMParameter;
import io.gcat.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        LineParser parser = LineParser.INSTANCE.reset();
        int c = parser.parseTimestamp(line);
        c = parser.parseBootTime(line, c);
        String rest = line.substring(c);
        if (!CMS && rest.startsWith("[GC (Allocation Failure) ")) {
            c = parser.parseNewRegion(line, c);
            c = parser.parseHeap(line, c);
            parser.parseGcTime(line, c);
            GCInfo gcInfo = parser.getGCInfo();
            gcInfo.setType(GCInfo.GCType.GC);
            gcInfo.setRegion(GCInfo.GCRegion.New);
            list.add(gcInfo);
        } else if (rest.startsWith("[GC (CMS Initial Mark)")) {
            CMS = true;
            CMSStartTimestamp = parser.getTimestamp();
        } else if (rest.startsWith("[CMS-concurrent-reset:")) {
            CMS = false;
            GCInfo gcInfo = parser.getGCInfo();
            gcInfo.setType(GCInfo.GCType.CMS);
            gcInfo.setRegion(GCInfo.GCRegion.Tenured);
            gcInfo.setGcTime(gcInfo.getTimestamp() - CMSStartTimestamp);
            list.add(gcInfo);
        }
    }

    @Override
    public String query(String sql) {
//        write(new File("/tmp/gcat.out"));
        Iterator<GCInfo> it = list.iterator();
        GCInfo first = it.next();
        long lastTimestamp = first.getTimestamp();
        long maxGcTime = first.getGcTime();
        long gcTimeSum = maxGcTime;
        long gcIntervalSum = 0;
        long maxGcInterval = 0;
        long gcCount = 1;
        long maxGcTimeTimestamp = 0;
        long maxGcIntervalTimestamp = 0;
        while (it.hasNext()) {
            GCInfo r = it.next();
            long t = r.getTimestamp();
            long gcTime = r.getGcTime();
            gcTimeSum += gcTime;
            if (maxGcTime < gcTime) {
                maxGcTime = gcTime;
                maxGcTimeTimestamp = t;
            }
            long interval = t - lastTimestamp;
            if (maxGcInterval < interval) {
                maxGcInterval = interval;
                maxGcIntervalTimestamp = t;
            }
            gcIntervalSum += interval;
            lastTimestamp = t;
            gcCount++;
        }

        logger.info("GC time avg: " + (gcTimeSum / gcCount) + " ms");
        logger.info("GC time max: " + (maxGcTime) + " ms");
        logger.info("GC time max timestamp: " + DateUtil.format(maxGcTimeTimestamp) + "(epoch: " + maxGcTimeTimestamp + ")");
        logger.info("GC interval avg: " + (gcIntervalSum / gcCount) + " ms");
        logger.info("GC interval max: " + (maxGcInterval) + " ms");
        logger.info("GC interval timestamp: " + DateUtil.format(maxGcIntervalTimestamp) + "(epoch: " + maxGcIntervalTimestamp + ")");

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

        private static final Logger logger = LoggerFactory.getLogger(LineParser.class);

        private GCInfo gcInfo = new GCInfo();

        private int parseTimestamp(String line) throws LineParseException {
            int s = "2019-05-05T15:52:07.063+0800".length();
            try {
                long timestamp = DateUtil.parse(line.substring(0, s));
                gcInfo.setTimestamp(timestamp);
                return s + 2; //skip ": "
            } catch (Exception e) {
                throw new LineParseException();
            }
        }

        private int parseBootTime(String line, int s) {
            int e = line.indexOf(": ", s);
            //parse timestamp
            Float bootTime = Float.valueOf(line.substring(s, e));
            gcInfo.setBootTime((long) (bootTime * 1000));

            return e + 2; //skip ": "
        }

        private int parseNewRegion(String line, int s) {
            s = line.indexOf("[ParNew: ", s) + "[ParNew: ".length();
            Matcher m = changePattern.matcher(line.substring(s, line.length()));
            if (!m.find()) {
                throw new IllegalArgumentException("not found new region change.");
            }
            Integer regionUsedBefore = Integer.valueOf(m.group(1));
            Integer regionUsedAfter = Integer.valueOf(m.group(2));
            Integer regionSize = Integer.valueOf(m.group(3));
            gcInfo.setRegionUsedBefore(regionUsedBefore);
            gcInfo.setRegionUsedAfter(regionUsedAfter);
            gcInfo.setRegionSize(regionSize);

            return s + m.group().length() + 2; //skip "] "
        }

        private int parseHeap(String line, int s) {
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

            return s + m.group().length() + 2; //skip "] "
        }

        private void parseGcTime(String line, int s) {
            Matcher m = gcTimePattern.matcher(line.substring(s, line.length()));
            if (m.find()) {
                Float gcTime = Float.valueOf(m.group(1));
                gcInfo.setGcTime((long) (gcTime * 1000));
            } else {
                throw new IllegalStateException("not found real time.");
            }
        }

        public LineParser reset() {
            gcInfo = new GCInfo();
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

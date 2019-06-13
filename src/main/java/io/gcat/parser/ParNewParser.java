package io.gcat.parser;

import io.gcat.entity.GCInfo;
import io.gcat.util.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.gcat.parser.CMSParNewParser.gcPausePtn;

enum ParNewParser {
    INSTANCE;

    private static Pattern changePtn = Pattern.compile("(\\d+)K->(\\d+)K\\((\\d+)K\\), ([0-9.]+) secs");

    private int offset;

    private String line;

    private GCInfo gcInfo;

    private void reset(long timestamp, long bootTime, String line, int offset) {
        this.line = line;
        this.offset = offset;
        this.gcInfo = new GCInfo();
        this.gcInfo.setTimestamp(timestamp);
        this.gcInfo.setBootTime(bootTime);
        this.gcInfo.setType(GCInfo.GCType.YongGC);
    }

    void parse(long timestamp, long bootTime, String line, int offset) {
        reset(timestamp, bootTime, line, offset);
        parseYoungGeneration();
        parseHeap();
        parseGcPause();
    }

    private void parseTimestamp() throws LineParseException {
        int s = "2019-05-05T15:52:07.063+0800".length();
        try {
            long timestamp = Utils.parse(line.substring(0, s));
            gcInfo.setTimestamp(timestamp);
            offset = s + 2; //skip ": "
        } catch (Exception e) {
            throw new LineParseException();
        }
    }

    private void parseBootTime() {
        int s = offset;
        int e = line.indexOf(": ", s);
        //parse timestamp
        Float bootTime = Float.valueOf(line.substring(s, e));
        gcInfo.setBootTime((long) (bootTime * 1000));

        offset = e + 2; //skip ": "
    }

    private void parseYoungGeneration() {
        int s = offset;
        s = line.indexOf("[ParNew: ", s) + "[ParNew: ".length();
        Matcher m = changePtn.matcher(line.substring(s, line.length()));
        if (!m.find()) {
            throw new IllegalArgumentException("not found new region change.");
        }
        Integer regionUsedBefore = Integer.valueOf(m.group(1));
        Integer regionUsedAfter = Integer.valueOf(m.group(2));
        Integer regionSize = Integer.valueOf(m.group(3));
        gcInfo.setYoungUsedBefore(regionUsedBefore);
        gcInfo.setYoungUsedAfter(regionUsedAfter);
        gcInfo.setYoungSize(regionSize);

        offset = s + m.group().length() + 2; //skip "] "
    }

    private void parseHeap() {
        int s = offset;
        Matcher m = changePtn.matcher(line.substring(s, line.length()));
        if (!m.find()) {
            throw new IllegalArgumentException("not found heap change.");
        }
        Integer heapUsedBefore = Integer.valueOf(m.group(1));
        Integer heapUsedAfter = Integer.valueOf(m.group(2));
        Integer heapSize = Integer.valueOf(m.group(3));

        gcInfo.setHeapUsedBefore(heapUsedBefore);
        gcInfo.setHeapUsedAfter(heapUsedAfter);
        gcInfo.setHeapSize(heapSize);

        offset = s + m.group().length() + 2; //skip "] "
    }

    private void parseGcPause() {
        int s = offset;
        Matcher m = gcPausePtn.matcher(line.substring(s, line.length()));
        if (m.find()) {
            Float gcPause = Float.valueOf(m.group(1));
            long gcPauseMilli = (long) (gcPause * 1000);
            gcInfo.addPause(gcPauseMilli, gcInfo.getTimestamp());
        } else {
            throw new IllegalStateException("not found real time.");
        }
    }

    boolean restStartWith(String prefix) {
        return line.substring(offset).startsWith(prefix);
    }

    public GCInfo getGCInfo() {
        return gcInfo;
    }
}
package io.gcat.parser;

import io.gcat.entity.GCInfo;

import java.util.regex.Matcher;

enum CMSParser {
    INSTANCE;

    private String line;

    private GCInfo gcInfo;

    public void parseInitialMarkLine(String line, long timestamp, long bootTime) {
        reset(line, timestamp, bootTime);
        Matcher matcher = CMSParNewParser.gcPausePtn.matcher(this.line);
        if (matcher.find()) {
            long gcPause = (long) (Float.valueOf(matcher.group(1)) * 1000);
            gcInfo.addPause(gcPause, timestamp);
        }
    }

    public void parseFinalMarkLine(String line, long timestamp, long bootTime) {
        this.line = line;
        Matcher matcher = CMSParNewParser.gcPausePtn.matcher(this.line);
        if (matcher.find()) {
            long gcPause = (long) (Float.valueOf(matcher.group(1)) * 1000);
            gcInfo.addPause(gcPause, timestamp);
        }
    }

    public GCInfo getGCInfo() {
        return gcInfo;
    }

    private void reset(String line, long timestamp, long bootTime) {
        this.line = line;
        this.gcInfo = new GCInfo();
        this.gcInfo.setTimestamp(timestamp);
        this.gcInfo.setBootTime(bootTime);
        this.gcInfo.setType(GCInfo.GCType.OldGC);
    }
}

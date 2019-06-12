package io.gcat.parser;

import io.gcat.entity.GCInfo;

import java.util.regex.Matcher;

public enum CMSParser {
    INSTANCE;

    private String line;

    private boolean inProcess;

    private long cmsPauseSum;

    private GCInfo gcInfo;

    public CMSParser reset(String line) {
        this.line = line;
        this.inProcess = true;
        this.cmsPauseSum = 0;
        this.gcInfo = new GCInfo();
        this.gcInfo.setType(GCInfo.GCType.OldGC);
        return this;
    }

    public void parseInitalMarkLine() {
        Matcher matcher = CMSParNewParser.gcPausePtn.matcher(line);
        if (matcher.find()) {
            Float gcPause = Float.valueOf(matcher.group(1));
            cmsPauseSum += (long) (gcPause * 1000);
        }
    }

    public void parseFinalMarkLine() {
        Matcher matcher = CMSParNewParser.gcPausePtn.matcher(line);
        if (matcher.find()) {
            Float gcPause = Float.valueOf(matcher.group(1));
            cmsPauseSum += (long) (gcPause * 1000);
        }
    }

    public CMSParser setLine(String line) {
        this.line = line;
        return this;
    }

    public GCInfo getGCInfo() {
        return gcInfo;
    }

    public void stop() {
        gcInfo.setGcPause(cmsPauseSum);
    }
}

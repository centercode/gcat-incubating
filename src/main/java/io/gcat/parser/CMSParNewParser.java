package io.gcat.parser;

import io.gcat.entity.GCInfo;
import io.gcat.entity.JVMParameter;
import io.gcat.summary.CMSParNewVisitor;
import io.gcat.summary.Summary;
import io.gcat.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMSParNewParser implements Parser {

    private static final Logger logger = LoggerFactory.getLogger(CMSParNewParser.class);

    static Pattern gcPausePtn = Pattern.compile("real=([0-9.]+) secs");

    static Pattern startPtn = Pattern.compile("^(\\d{4}-\\d\\d-\\d\\dT\\d\\d:\\d\\d:\\d\\d.\\d{3}\\+\\d{4}): ([\\d.]+): ");

    private JVMParameter jvmParameter;

    private long lineCount = 0;

    private ParNewParser parNewParser = ParNewParser.INSTANCE;

    private CMSParser cmsParser = CMSParser.INSTANCE;

    private long endTimestamp;

    private long endBootTime;

    private List<GCInfo> list = new LinkedList<>();

    public CMSParNewParser(JVMParameter jvmParameter) {
        this.jvmParameter = jvmParameter;
    }

    @Override
    public void feed(String line) {
        lineCount++;
        try {
            parse(line);
        } catch (LineParseException e) {
            int end = line.length() < 30 ? line.length() : 30;
            String shortLine = line.substring(0, end);
            String message = "not parseable line#{}:{}";
            logger.warn(message, lineCount, shortLine);
        }
    }

    private void parse(String line) throws LineParseException {
        Matcher matcher = startPtn.matcher(line);
        if (!matcher.find()) {
            return;
        }
        int offset = matcher.group().length();
        long timestamp = Utils.parse(matcher.group(1));
        long bootTime = (long) (Float.valueOf(matcher.group(2)) * 1000);
        if (line.startsWith("[GC (Allocation Failure) ", offset)) {
            parNewParser.parse(timestamp, bootTime, line, offset);
            GCInfo gcInfo = parNewParser.getGCInfo();
            list.add(gcInfo);
            recordEnd(timestamp, bootTime);
        } else if (line.startsWith("[GC (CMS Initial Mark)", offset)) {
            cmsParser.parseInitialMarkLine(line, timestamp, bootTime);
            GCInfo gcInfo = cmsParser.getGCInfo();
            list.add(gcInfo);
        } else if (line.startsWith("[GC (CMS Final Remark)", offset)) {
            cmsParser.parseFinalMarkLine(line, timestamp, bootTime);
        } else if (line.startsWith("[CMS-concurrent-reset:", offset)) {
            recordEnd(timestamp, bootTime);
        }
    }

    private void recordEnd(long timestamp, long bootTime) {
        endTimestamp = timestamp;
        endBootTime = bootTime;
    }

    @Override
    public String query(String sql) {
        CMSParNewVisitor visitor = CMSParNewVisitor.of(jvmParameter);
        visitor.visit(list);
        visitor.setLastTimestamp(endTimestamp);
        Summary heapSummary = Summary.create(visitor);
        System.out.println(heapSummary);
        return null;
    }
}

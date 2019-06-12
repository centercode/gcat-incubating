package io.gcat.parser;

import io.gcat.entity.GCInfo;
import io.gcat.entity.JVMParameter;
import io.gcat.summary.Summary;
import io.gcat.summary.Visitor;
import io.gcat.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
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

    private List<GCInfo> list = new LinkedList<>();

    public CMSParNewParser(JVMParameter jvmParameter) {
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

    @Override
    public void stop() {
        list.sort(Comparator.comparingLong(GCInfo::getTimestamp));
    }

    private void parseLineInternal(String line) throws LineParseException {
        Matcher matcher = startPtn.matcher(line);
        if (!matcher.find()) {
            return;
        }
        int offset = matcher.group().length();
        long timestamp = Utils.parse(matcher.group(1));
        long bootTime = (long) (Float.valueOf(matcher.group(2)) * 1000);
        if (line.startsWith("[GC (Allocation Failure) ", offset)) {
            parNewParser.reset(line, offset);
            parNewParser.parseYoungGeneration();
            parNewParser.parseHeap();
            parNewParser.parseGcPause();
            GCInfo gcInfo = parNewParser.getGCInfo();
            gcInfo.setTimestamp(timestamp);
            gcInfo.setBootTime(bootTime);
            list.add(gcInfo);
        } else if (line.startsWith("[GC (CMS Initial Mark)", offset)) {
            cmsParser.reset(line);
            cmsParser.parseInitalMarkLine();
            GCInfo gcInfo = cmsParser.getGCInfo();
            gcInfo.setTimestamp(timestamp);
            gcInfo.setBootTime(bootTime);
            list.add(gcInfo);
        } else if (line.startsWith("[GC (CMS Final Remark)", offset)) {
            cmsParser.setLine(line);
            cmsParser.parseFinalMarkLine();
        } else if (line.startsWith("[CMS-concurrent-reset:", offset)) {
            cmsParser.stop();
        }
    }

    @Override
    public String query(String sql) {
//        write(new File("/tmp/gcat.out"));
        Visitor visitor = Visitor.of(jvmParameter);
        visitor.visit(list);
        Summary heapSummary = Summary.create(visitor);
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
}

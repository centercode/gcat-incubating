package io.gcat.parser;

import io.gcat.entity.GCInfo;
import io.gcat.entity.JVMParameter;
import io.gcat.summary.HeapSize;
import io.gcat.summary.Summary;
import io.gcat.util.Utils;
import io.gcat.visitor.GCInterval;
import io.gcat.visitor.GCPause;
import io.gcat.visitor.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
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
        CMSParNewVisitor visitor = new CMSParNewVisitor(jvmParameter);
        visitor.visit(list);
        visitor.setLastTimestamp(endTimestamp);
        Summary heapSummary = Summary.create(visitor);
        System.out.println(heapSummary);
        return null;
    }

    private class CMSParNewVisitor implements Visitor {

        private JVMParameter jvmParameter;

        private HeapSize heapSize;

        private long firstTimestamp;

        private long lastTimestamp;

        private int youngGcCount;

        private int oldGcCount;

        private GCPause gcPause = new GCPause();

        private GCInterval gcInterval = new GCInterval();

        private CMSParNewVisitor(JVMParameter jvmParameter) {
            this.jvmParameter = jvmParameter;
            initHeapSize();
        }

        private void initHeapSize() {
            long initialHeapSize = jvmParameter.getLong("InitialHeapSize");
            long maxHeapSize = jvmParameter.getLong("MaxHeapSize");
            long newSize = jvmParameter.getLong("NewSize");
            long maxNewSize = jvmParameter.getLong("MaxNewSize");
            this.heapSize = new HeapSize(initialHeapSize, maxHeapSize, newSize, maxNewSize);
        }

        private void visit(List<GCInfo> list) {
            Iterator<GCInfo> it = list.iterator();
            visitFirst(it.next());
            while (it.hasNext()) {
                visitRest(it.next());
            }
            gcPause.setCount(youngGcCount + oldGcCount * 2);
            gcInterval.setCount(youngGcCount + oldGcCount);
        }

        private void visitFirst(GCInfo first) {
            visitHeapSize(first);
            this.firstTimestamp = first.getTimestamp();
            this.lastTimestamp = first.getTimestamp();
            gcPause.setPauseSum(first.getGcPauseSum())
                    .setMaxPause(first.getMaxGcPause())
                    .setMaxPauseTimestamp(first.getMaxGcPauseTimestamp());
            gcInterval.setIntervalSum(0)
                    .setMinInterval(Long.MAX_VALUE)
                    .setMinIntervalTimestamp(Long.MIN_VALUE);
            setCount(first);
        }

        private void setCount(GCInfo gcInfo) {
            if (gcInfo.getType() == GCInfo.GCType.YongGC) {
                this.youngGcCount++;
            } else if (gcInfo.getType() == GCInfo.GCType.OldGC) {
                this.oldGcCount++;
            }
        }

        private void visitRest(GCInfo r) {
            visitHeapSize(r);
            long ts = r.getTimestamp();
            long gcPauseSum = r.getGcPauseSum();
            long interval = ts - lastTimestamp;
            this.gcPause.addPause(gcPauseSum, ts);
            this.gcInterval.addInterval(interval, ts);
            lastTimestamp = ts;
            setCount(r);
        }

        private void visitHeapSize(GCInfo info) {
            int heapSize = info.getHeapSize();
            int youngSize = info.getYoungSize();
            int youngUsedBefore = info.getYoungUsedBefore();
            int youngUsedAfter = info.getYoungUsedAfter();
            int heapUsedBefore = info.getHeapUsedBefore();
            int heapUsedAfter = info.getHeapUsedAfter();
            int r = jvmParameter.getInt("SurvivorRatio");
            int survivorSize = youngSize / (r + 1);

            this.heapSize.casYoungAllocated(youngSize + survivorSize);
            this.heapSize.casOldAllocated(heapSize - youngSize);
            this.heapSize.casYoungPeak(youngUsedBefore);

            int oldBefore = heapUsedBefore - youngUsedBefore;
            int oldAfter = heapUsedAfter - youngUsedAfter;
            this.heapSize.casOldPeak(Math.max(oldBefore, oldAfter));
        }

        private void setLastTimestamp(long lastTimestamp) {
            this.lastTimestamp = lastTimestamp;
        }

        @Override
        public long getDuration() {
            return lastTimestamp - firstTimestamp;
        }

        @Override
        public HeapSize getHeapSize() {
            return heapSize;
        }

        @Override
        public GCPause getGCPause() {
            return gcPause;
        }

        @Override
        public GCInterval getGCInterval() {
            return gcInterval;
        }

        @Override
        public double getThroughput() {
            return 1 - 1.0 * gcPause.getPauseSum() / getDuration();
        }

        @Override
        public int getGCCount() {
            return youngGcCount;
        }
    }
}

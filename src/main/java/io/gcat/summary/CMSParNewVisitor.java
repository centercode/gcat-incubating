package io.gcat.summary;

import io.gcat.entity.GCInfo;
import io.gcat.entity.JVMParameter;
import io.gcat.visitor.GCInterval;
import io.gcat.visitor.GCPause;
import io.gcat.visitor.Visitor;

import java.util.Iterator;
import java.util.List;

public class CMSParNewVisitor implements Visitor {

    private JVMParameter jvmParameter;

    private HeapSize heapSize;

    private long firstTimestamp;

    private long lastTimestamp;

    private int youngGcCount;

    private int oldGcCount;

    private GCPause gcPause = new GCPause();

    private GCInterval gcInterval = new GCInterval();

    public CMSParNewVisitor(JVMParameter jvmParameter) {
        this.jvmParameter = jvmParameter;
        initHeapSize();
    }

    public static CMSParNewVisitor of(JVMParameter jvmParameter) {
        return new CMSParNewVisitor(jvmParameter);
    }

    private void initHeapSize() {
        long initialHeapSize = jvmParameter.getLong("InitialHeapSize");
        long maxHeapSize = jvmParameter.getLong("MaxHeapSize");
        long newSize = jvmParameter.getLong("NewSize");
        long maxNewSize = jvmParameter.getLong("MaxNewSize");
        this.heapSize = new HeapSize(initialHeapSize, maxHeapSize, newSize, maxNewSize);
    }

    public void visit(List<GCInfo> list) {
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

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    @Override
    public long getDuration() {
        return lastTimestamp - firstTimestamp;
    }

    public HeapSize getHeapSize() {
        return heapSize;
    }


    public GCPause getGCPause() {
        return gcPause;
    }

    public GCInterval getGCInterval() {
        return gcInterval;
    }

    @Override
    public double getThroughput() {
        return 1 - 1.0 * gcPause.getPauseSum() / getDuration();
    }

    public int getGCCount() {
        return youngGcCount;
    }
}

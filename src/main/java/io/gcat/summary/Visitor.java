package io.gcat.summary;

import io.gcat.entity.GCInfo;
import io.gcat.entity.JVMParameter;

import java.util.Iterator;
import java.util.List;

public class Visitor {

    private JVMParameter jvmParameter;

    private HeapSize heapSize;

    private long firstTimestamp;

    private long lastTimestamp;

    private int count;

    private long pauseSum;

    private long maxPause;

    private long maxPauseTimestamp;

    private long intervalSum;

    private long minInterval;

    private long minIntervalTimestamp;

    public Visitor(JVMParameter jvmParameter) {
        this.jvmParameter = jvmParameter;
        initHeapSize();
    }

    public static Visitor of(JVMParameter jvmParameter) {
        return new Visitor(jvmParameter);
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

    private void visitFirst(GCInfo first) {
        visitHeapSize(first);
        this.firstTimestamp = first.getTimestamp();
        this.lastTimestamp = first.getTimestamp();
        this.pauseSum = first.getGcPause();
        this.maxPause = first.getGcPause();
        this.maxPauseTimestamp = first.getTimestamp();
        this.intervalSum = 0;
        this.minInterval = Long.MAX_VALUE;
        this.minIntervalTimestamp = Long.MIN_VALUE;
        this.count = 1;
    }

    private void visitRest(GCInfo r) {
        visitHeapSize(r);
        long ts = r.getTimestamp();
        long gcPause = r.getGcPause();
        long interval = ts - lastTimestamp;
        addPause(gcPause, ts);
        addInterval(interval, ts);
        lastTimestamp = ts;
        count++;
    }

    private void addPause(long gcPause, long t) {
        pauseSum += gcPause;
        if (maxPause < gcPause) {
            maxPause = gcPause;
            maxPauseTimestamp = t;
        }
    }

    private void addInterval(long interval, long t) {
        intervalSum += interval;
        if (interval < minInterval) {
            minInterval = interval;
            minIntervalTimestamp = t;
        }
    }

    public HeapSize getHeapSize() {
        return heapSize;
    }

    public long getFirstTimestamp() {
        return firstTimestamp;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public int getCount() {
        return count;
    }

    public long getPauseSum() {
        return pauseSum;
    }

    public long getMaxPause() {
        return maxPause;
    }

    public long getMaxPauseTimestamp() {
        return maxPauseTimestamp;
    }

    public long getIntervalSum() {
        return intervalSum;
    }

    public long getMinInterval() {
        return minInterval;
    }

    public long getMinIntervalTimestamp() {
        return minIntervalTimestamp;
    }
}

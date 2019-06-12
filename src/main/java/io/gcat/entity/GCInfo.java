package io.gcat.entity;

/**
 * size in KB
 */
public class GCInfo {

    /**
     * in milliSeconds
     */
    private long timestamp;

    /**
     * in milliSeconds
     */
    private long bootTime;

    private GCType type;

    /**
     * Young Generation used size before gc, peak size, eden + one survivor
     */
    private int youngUsedBefore;

    /**
     * Young Generation used size after gc, eden + one survivor
     */
    private int youngUsedAfter;

    /**
     * Young Generation size, eden + one survivor
     */
    private int youngSize;

    /**
     * Heap used size before gc, eden + one survivor + old
     */
    private int heapUsedBefore;

    /**
     * Heap used size after gc, eden + one survivor + old
     */
    private int heapUsedAfter;

    /**
     * Heap size, eden + one survivor + old
     */
    private int heapSize;

    /**
     * gc Stop-The-World time, in milliSeconds
     */
    private long gcPause;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getBootTime() {
        return bootTime;
    }

    public void setBootTime(long bootTime) {
        this.bootTime = bootTime;
    }

    public GCType getType() {
        return type;
    }

    public void setType(GCType type) {
        this.type = type;
    }

    public int getYoungUsedBefore() {
        return youngUsedBefore;
    }

    public void setYoungUsedBefore(int youngUsedBefore) {
        this.youngUsedBefore = youngUsedBefore;
    }

    public int getYoungUsedAfter() {
        return youngUsedAfter;
    }

    public void setYoungUsedAfter(int youngUsedAfter) {
        this.youngUsedAfter = youngUsedAfter;
    }

    public int getYoungSize() {
        return youngSize;
    }

    public void setYoungSize(int youngSize) {
        this.youngSize = youngSize;
    }

    public int getHeapUsedBefore() {
        return heapUsedBefore;
    }

    public void setHeapUsedBefore(int heapUsedBefore) {
        this.heapUsedBefore = heapUsedBefore;
    }

    public int getHeapUsedAfter() {
        return heapUsedAfter;
    }

    public void setHeapUsedAfter(int heapUsedAfter) {
        this.heapUsedAfter = heapUsedAfter;
    }

    public int getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(int heapSize) {
        this.heapSize = heapSize;
    }

    public long getGcPause() {
        return gcPause;
    }

    public void setGcPause(long gcPause) {
        this.gcPause = gcPause;
    }

    @Override
    public String toString() {
        return "{" +
                "timestamp=" + timestamp +
                ", bootTime=" + bootTime +
                ", type=" + type +
                ", youngUsedBefore=" + youngUsedBefore +
                ", youngUsedAfter=" + youngUsedAfter +
                ", youngSize=" + youngSize +
                ", heapUsedBefore=" + heapUsedBefore +
                ", heapUsedAfter=" + heapUsedAfter +
                ", heapSize=" + heapSize +
                ", gcPause=" + gcPause +
                '}';
    }

    public GCInfo copy() {
        GCInfo copy = new GCInfo();
        copy.setTimestamp(timestamp);
        copy.setBootTime(bootTime);
        copy.setType(type);
        copy.setYoungUsedBefore(youngUsedBefore);
        copy.setYoungUsedAfter(youngUsedAfter);
        copy.setYoungSize(youngSize);
        copy.setHeapUsedBefore(heapUsedBefore);
        copy.setHeapUsedAfter(heapUsedAfter);
        copy.setHeapSize(heapSize);
        copy.setGcPause(gcPause);

        return copy;
    }

    public enum GCType {
        YongGC, OldGC, MixedGC, FullGC
    }
}

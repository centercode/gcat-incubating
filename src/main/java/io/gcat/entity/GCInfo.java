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
    private long gcPauseSum;

    private long maxGcPause;

    private long maxGcPauseTimestamp;

    public void addPause(long pause, long timestamp) {
        gcPauseSum += pause;
        if (maxGcPause < pause) {
            maxGcPause = pause;
            maxGcPauseTimestamp = timestamp;
        }
    }

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

    public long getGcPauseSum() {
        return gcPauseSum;
    }

    public void setGcPauseSum(long gcPauseSum) {
        this.gcPauseSum = gcPauseSum;
    }

    public long getMaxGcPause() {
        return maxGcPause;
    }

    public long getMaxGcPauseTimestamp() {
        return maxGcPauseTimestamp;
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
                ", gcPause=" + gcPauseSum +
                '}';
    }

    public enum GCType {
        YongGC, OldGC, MixedGC, FullGC
    }
}

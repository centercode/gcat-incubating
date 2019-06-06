package io.gcat;

/**
 * size in K, time in milliSeconds
 */
public class GCInfo {

    private long timestamp;

    private long bootTime;

    private GCType type;

    private GCRegion region;

    private int regionUsedBefore;

    private int regionUsedAfter;

    private int regionSize;

    private int heapUsedBefore;

    private int heapUsedAfter;

    private int heapSize;

    private long gcTime;

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

    public GCRegion getRegion() {
        return region;
    }

    public void setRegion(GCRegion region) {
        this.region = region;
    }

    public int getRegionUsedBefore() {
        return regionUsedBefore;
    }

    public void setRegionUsedBefore(int regionUsedBefore) {
        this.regionUsedBefore = regionUsedBefore;
    }

    public int getRegionUsedAfter() {
        return regionUsedAfter;
    }

    public void setRegionUsedAfter(int regionUsedAfter) {
        this.regionUsedAfter = regionUsedAfter;
    }

    public int getRegionSize() {
        return regionSize;
    }

    public void setRegionSize(int regionSize) {
        this.regionSize = regionSize;
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

    public long getGcTime() {
        return gcTime;
    }

    public void setGcTime(long gcTime) {
        this.gcTime = gcTime;
    }

    @Override
    public String toString() {
        return "{" +
                "timestamp=" + timestamp +
                ", bootTime=" + bootTime +
                ", type=" + type +
                ", region=" + region +
                ", regionUsedBefore=" + regionUsedBefore +
                ", regionUsedAfter=" + regionUsedAfter +
                ", regionSize=" + regionSize +
                ", heapUsedBefore=" + heapUsedBefore +
                ", heapUsedAfter=" + heapUsedAfter +
                ", heapSize=" + heapSize +
                ", gcTime=" + gcTime +
                '}';
    }

    public GCInfo copy() {
        GCInfo copy = new GCInfo();
        copy.setTimestamp(timestamp);
        copy.setBootTime(bootTime);
        copy.setRegionUsedBefore(regionUsedBefore);
        copy.setRegionUsedAfter(regionUsedAfter);
        copy.setRegionSize(regionSize);
        copy.setHeapUsedBefore(heapUsedBefore);
        copy.setHeapUsedAfter(heapUsedAfter     );
        copy.setHeapSize(heapSize);
        copy.setGcTime(gcTime);

        return copy;
    }

    public enum GCType {
        GC, CMS
    }

    public enum GCRegion {
        New, Tentured, Perm
    }
}

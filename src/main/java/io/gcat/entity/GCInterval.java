package io.gcat.entity;

public class GCInterval {

    private long count;

    private long intervalSum;

    private long minInterval;

    private long minIntervalTimestamp;

    public void addInterval(long interval, long t) {
        intervalSum += interval;
        if (interval < minInterval) {
            minInterval = interval;
            minIntervalTimestamp = t;
        }
    }

    public long getCount() {
        return count;
    }

    public GCInterval setCount(long count) {
        this.count = count;
        return this;
    }

    public long getIntervalSum() {
        return intervalSum;
    }

    public GCInterval setIntervalSum(long intervalSum) {
        this.intervalSum = intervalSum;
        return this;
    }

    public long getMinInterval() {
        return minInterval;
    }

    public GCInterval setMinInterval(long minInterval) {
        this.minInterval = minInterval;
        return this;
    }

    public long getMinIntervalTimestamp() {
        return minIntervalTimestamp;
    }

    public GCInterval setMinIntervalTimestamp(long minIntervalTimestamp) {
        this.minIntervalTimestamp = minIntervalTimestamp;
        return this;
    }

    public long getAvgInterval() {
        return intervalSum / (count - 1);
    }
}

package io.gcat.summary;

import io.gcat.entity.GCInfo;

public class Visitor {

    private long firstTimestamp;

    private long lastTimestamp;

    private int count;

    private long pauseSum;

    private long maxPause;

    private long maxPauseTimestamp;

    private long intervalSum;

    private long minInterval;

    private long minIntervalTimestamp;

    public static Visitor create(GCInfo first) {
        Visitor visitor = new Visitor();
        visitor.firstTimestamp = first.getTimestamp();
        visitor.lastTimestamp = first.getTimestamp();
        visitor.pauseSum = first.getGcPause();
        visitor.maxPause = first.getGcPause();
        visitor.maxPauseTimestamp = Long.MIN_VALUE;
        visitor.intervalSum = 0;
        visitor.minInterval = Long.MAX_VALUE;
        visitor.minIntervalTimestamp = Long.MIN_VALUE;
        visitor.count = 1;
        return visitor;
    }

    public Visitor incr() {
        count++;
        return this;
    }

    public Visitor addPause(long gcPause, long t) {
        pauseSum += gcPause;
        if (maxPause < gcPause) {
            maxPause = gcPause;
            maxPauseTimestamp = t;
        }

        return this;
    }

    public Visitor addInterval(long interval, long t) {
        intervalSum += interval;
        if (interval < minInterval) {
            minInterval = interval;
            minIntervalTimestamp = t;
        }
        return this;
    }

    public long getFirstTimestamp() {
        return firstTimestamp;
    }

    public Visitor setFirstTimestamp(long firstTimestamp) {
        this.firstTimestamp = firstTimestamp;
        return this;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public Visitor setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Visitor setCount(int count) {
        this.count = count;
        return this;
    }

    public long getPauseSum() {
        return pauseSum;
    }

    public Visitor setPauseSum(long pauseSum) {
        this.pauseSum = pauseSum;
        return this;
    }

    public long getMaxPause() {
        return maxPause;
    }

    public Visitor setMaxPause(long maxPause) {
        this.maxPause = maxPause;
        return this;
    }

    public long getMaxPauseTimestamp() {
        return maxPauseTimestamp;
    }

    public Visitor setMaxPauseTimestamp(long maxPauseTimestamp) {
        this.maxPauseTimestamp = maxPauseTimestamp;
        return this;
    }

    public long getIntervalSum() {
        return intervalSum;
    }

    public Visitor setIntervalSum(long intervalSum) {
        this.intervalSum = intervalSum;
        return this;
    }

    public long getMinInterval() {
        return minInterval;
    }

    public Visitor setMinInterval(long minInterval) {
        this.minInterval = minInterval;
        return this;
    }

    public long getMinIntervalTimestamp() {
        return minIntervalTimestamp;
    }

    public Visitor setMinIntervalTimestamp(long minIntervalTimestamp) {
        this.minIntervalTimestamp = minIntervalTimestamp;
        return this;
    }
}

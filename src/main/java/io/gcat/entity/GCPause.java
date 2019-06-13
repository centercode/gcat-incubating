package io.gcat.entity;

public class GCPause {

    private long count;

    private long pauseSum;

    private long maxPause;

    private long maxPauseTimestamp;

    public void addPause(long gcPause, long t) {
        pauseSum += gcPause;
        if (maxPause < gcPause) {
            maxPause = gcPause;
            maxPauseTimestamp = t;
        }
    }

    public long getCount() {
        return count;
    }

    public GCPause setCount(long count) {
        this.count = count;
        return this;
    }

    public long getPauseSum() {
        return pauseSum;
    }

    public GCPause setPauseSum(long pauseSum) {
        this.pauseSum = pauseSum;
        return this;
    }

    public long getMaxPause() {
        return maxPause;
    }

    public GCPause setMaxPause(long maxPause) {
        this.maxPause = maxPause;
        return this;
    }

    public long getMaxPauseTimestamp() {
        return maxPauseTimestamp;
    }

    public GCPause setMaxPauseTimestamp(long maxPauseTimestamp) {
        this.maxPauseTimestamp = maxPauseTimestamp;
        return this;
    }

    public long getAvgPause() {
        return pauseSum / count;
    }
}

package io.gcat.parser;

import io.gcat.util.DateUtil;

public class Summary {

    private String name;

    private int count;

    private long avgTime;

    private long maxTime;

    private long maxTimeTimestamp;

    private long avgInterval;

    private long minInterval;

    private long minIntervalTimestamp;

    public String getName() {
        return name;
    }

    public Summary setName(String name) {
        this.name = name;
        return this;
    }

    public int getCount() {
        return count;
    }

    public Summary setCount(int count) {
        this.count = count;
        return this;
    }

    public long getAvgTime() {
        return avgTime;
    }

    public Summary setAvgTime(long avgTime) {
        this.avgTime = avgTime;
        return this;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public Summary setMaxTime(long maxTime) {
        this.maxTime = maxTime;
        return this;
    }

    public long getMaxTimeTimestamp() {
        return maxTimeTimestamp;
    }

    public Summary setMaxTimeTimestamp(long maxTimeTimestamp) {
        this.maxTimeTimestamp = maxTimeTimestamp;
        return this;
    }

    public long getAvgInterval() {
        return avgInterval;
    }

    public Summary setAvgInterval(long avgInterval) {
        this.avgInterval = avgInterval;
        return this;
    }

    public long getMinInterval() {
        return minInterval;
    }

    public Summary setMinInterval(long minInterval) {
        this.minInterval = minInterval;
        return this;
    }

    public long getMinIntervalTimestamp() {
        return minIntervalTimestamp;
    }

    public Summary setMinIntervalTimestamp(long minIntervalTimestamp) {
        this.minIntervalTimestamp = minIntervalTimestamp;
        return this;
    }

    @Override
    public String toString() {
        return "\n" + name + " GC Count: " + count + "\n" +
                name + " GC Time:\n" +
                "\tavg: " + avgTime + " ms\n" +
                "\tmax: " + maxTime + " ms" +
                "(at " + DateUtil.format(maxTimeTimestamp) +
                ", epoch: " + maxTimeTimestamp + ")\n" +
                name + " GC Interval:\n" +
                "\tavg: " + (avgInterval) + " ms\n" +
                "\tmin: " + (minInterval) + " ms" +
                "(at " + DateUtil.format(minIntervalTimestamp) +
                ", epoch: " + minIntervalTimestamp + ")\n";
    }
}

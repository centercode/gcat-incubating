package io.gcat.summary;

import io.gcat.util.Utils;

public class Summary {

    private HeapSize heapSize;

    private String name;

    private int count;

    private long duration;

    private long avgPause;

    private long maxPause;

    private long maxPauseTimestamp;

    private long avgInterval;

    private long minInterval;

    private long minIntervalTimestamp;

    private double throughput;

    public static Summary create(Visitor visitor) {
        long d = visitor.getLastTimestamp() - visitor.getFirstTimestamp();
        return new Summary()
                .setName("Heap")
                .setHeapSize(visitor.getHeapSize())
                .setCount(visitor.getCount())
                .setDuration(d)
                .setAvgPause(visitor.getPauseSum() / visitor.getCount())
                .setMaxPause(visitor.getMaxPause())
                .setMaxPauseTimestamp(visitor.getMaxPauseTimestamp())
                .setAvgInterval(visitor.getIntervalSum() / (visitor.getCount() - 1))
                .setMinInterval(visitor.getMinInterval())
                .setMinIntervalTimestamp(visitor.getMinIntervalTimestamp())
                .setThroughput(1 - 1.0 * visitor.getPauseSum() / d);
    }

    public Summary setHeapSize(HeapSize heapSize) {
        this.heapSize = heapSize;
        return this;
    }

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

    public long getDuration() {
        return duration;
    }

    public Summary setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public long getAvgPause() {
        return avgPause;
    }

    public Summary setAvgPause(long avgPause) {
        this.avgPause = avgPause;
        return this;
    }

    public long getMaxPause() {
        return maxPause;
    }

    public Summary setMaxPause(long maxPause) {
        this.maxPause = maxPause;
        return this;
    }

    public long getMaxPauseTimestamp() {
        return maxPauseTimestamp;
    }

    public Summary setMaxPauseTimestamp(long maxPauseTimestamp) {
        this.maxPauseTimestamp = maxPauseTimestamp;
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

    public double getThroughput() {
        return throughput;
    }

    public Summary setThroughput(double throughput) {
        this.throughput = throughput;
        return this;
    }

    @Override
    public String toString() {
        return "\n" + heapSize.toString() +
                "Throughput: " + Utils.format(throughput) +
                name + " GC Duration:" + Utils.formatDuration(duration) + "\n" +
                name + " GC Count: " + count + "\n" +
                name + " GC Pause Time:\n" +
                "\tavg: " + avgPause + " ms\n" +
                "\tmax: " + maxPause + " ms" +
                "(at " + Utils.formatDate(maxPauseTimestamp) + ")\n" +
                name + " GC Interval:\n" +
                "\tavg: " + (avgInterval) + " ms\n" +
                "\tmin: " + (minInterval) + " ms" +
                "(at " + Utils.formatDate(minIntervalTimestamp) + ")\n";
    }
}

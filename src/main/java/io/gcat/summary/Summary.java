package io.gcat.summary;

import io.gcat.util.DateUtil;

import java.time.Duration;

public class Summary {

    private String name;

    private int count;

    private Duration duration;

    private long avgPause;

    private long maxPause;

    private long maxPauseTimestamp;

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

    public Duration getDuration() {
        return duration;
    }

    public Summary setDuration(Duration duration) {
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

    @Override
    public String toString() {
        String durationStr = String.format("%sHour %smin %ssec",
                duration.toHours(),
                duration.toMinutes() % 60,
                duration.getSeconds() % 60);

        return "\n" + name + " GC Duration:" + durationStr + "\n" +
                name + " GC Count: " + count + "\n" +
                name + " GC Pause Time:\n" +
                "\tavg: " + avgPause + " ms\n" +
                "\tmax: " + maxPause + " ms" +
                "(at " + DateUtil.format(maxPauseTimestamp) +
                ", epoch: " + maxPauseTimestamp + ")\n" +
                name + " GC Interval:\n" +
                "\tavg: " + (avgInterval) + " ms\n" +
                "\tmin: " + (minInterval) + " ms" +
                "(at " + DateUtil.format(minIntervalTimestamp) +
                ", epoch: " + minIntervalTimestamp + ")\n";
    }
}

package io.gcat.summary;

import io.gcat.entity.GCInterval;
import io.gcat.entity.GCPause;
import io.gcat.entity.HeapSize;
import io.gcat.util.Utils;
import io.gcat.visitor.Visitor;

public class Summary {

    private HeapSize heapSize;

    private int gcCount;

    private long duration;

    private GCPause gcPause;

    private GCInterval gcInterval;

    private double throughput;

    public static Summary create(Visitor visitor) {
        return new Summary()
                .setHeapSize(visitor.getHeapSize())
                .setGcCount(visitor.getGCCount())
                .setDuration(visitor.getDuration())
                .setGcPause(visitor.getGCPause())
                .setGcInterval(visitor.getGCInterval())
                .setThroughput(visitor.getThroughput());
    }

    public Summary setHeapSize(HeapSize heapSize) {
        this.heapSize = heapSize;
        return this;
    }

    public Summary setGcCount(int gcCount) {
        this.gcCount = gcCount;
        return this;
    }

    public Summary setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public Summary setThroughput(double throughput) {
        this.throughput = throughput;
        return this;
    }

    public Summary setGcPause(GCPause gcPause) {
        this.gcPause = gcPause;
        return this;
    }

    public Summary setGcInterval(GCInterval gcInterval) {
        this.gcInterval = gcInterval;
        return this;
    }

    @Override
    public String toString() {
        return "\n" + heapSize.toString() +
                "Throughput: " + Utils.format(throughput) +
                "Heap GC Duration:" + Utils.formatDuration(duration) + "\n" +
                "Heap GC Count: " + gcCount + "\n" +
                " GC Pause Time:\n" +
                "\tavg: " + gcPause.getAvgPause() + " ms\n" +
                "\tmax: " + gcPause.getMaxPause() + " ms" +
                "(at " + Utils.formatDate(gcPause.getMaxPauseTimestamp()) + ")\n" +
                "Heap GC Interval:\n" +
                "\tavg: " + gcInterval.getAvgInterval() + " ms\n" +
                "\tmin: " + gcInterval.getMinInterval() + " ms" +
                "(at " + Utils.formatDate(gcInterval.getMinIntervalTimestamp()) + ")\n";
    }
}

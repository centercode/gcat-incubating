package io.gcat.summary;

import io.gcat.util.SizeUtil;

public class HeapSize {

    private long initialHeapSize;

    private long maxHeapSize;

    private boolean heapSizeFixed;

    private long newSize;

    private long maxNewSize;

    private boolean newSizeFixed;

    private int youngAllocated;

    private int youngPeak;

    private int oldAllocated;

    private int oldPeak;

    private int metaspaceAllocated;

    private int metaspacePeak;

    public HeapSize(long initialHeapSize, long maxHeapSize, long newSize, long maxNewSize) {
        this.initialHeapSize = initialHeapSize;
        this.maxHeapSize = maxHeapSize;
        this.newSize = newSize;
        this.maxNewSize = maxNewSize;
        this.heapSizeFixed = initialHeapSize == maxHeapSize;
        this.newSizeFixed = newSize == maxNewSize;
        if (newSizeFixed) {
            //todo
            youngAllocated = (int) (newSize / 1024);
            if (heapSizeFixed) {
                oldAllocated = (int) ((initialHeapSize - newSize) / 1024);
            }
        }
    }

    public void casYoungAllocated(int youngAllocated) {
        if (this.maxNewSize < youngAllocated) {
            throw new IllegalStateException();
        }
        if (newSizeFixed) {
            return;
        }
        this.youngAllocated = youngAllocated;
    }

    public void casYoungPeak(int youngPeak) {
        if (this.youngPeak < youngPeak) {
            this.youngPeak = youngPeak;
        }
    }

    public void casOldAllocated(int oldAllocated) {
        if (heapSizeFixed && newSizeFixed) {
            return;
        }
        this.oldAllocated = oldAllocated;
    }

    public void casOldPeak(int oldPeak) {
        if (this.oldPeak < oldPeak) {
            this.oldPeak = oldPeak;
        }
    }

    @Override
    public String toString() {
        return "Heap Size:\n" +
                "\tyoung: " + SizeUtil.format(youngAllocated) + "(Allocated) " +
                SizeUtil.format(youngPeak) + "(Peak)\n" +
                "\told: " + SizeUtil.format(oldAllocated) + "(Allocated) " +
                SizeUtil.format(oldPeak) + "(Peak)\n";
    }
}

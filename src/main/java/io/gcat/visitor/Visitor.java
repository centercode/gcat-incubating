package io.gcat.visitor;

import io.gcat.summary.HeapSize;

public interface Visitor {

    int getGCCount();

    long getDuration();

    double getThroughput();

    HeapSize getHeapSize();

    GCPause getGCPause();

    GCInterval getGCInterval();
}

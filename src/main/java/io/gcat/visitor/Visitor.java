package io.gcat.visitor;

import io.gcat.entity.GCInterval;
import io.gcat.entity.GCPause;
import io.gcat.entity.HeapSize;

public interface Visitor {

    int getGCCount();

    long getDuration();

    double getThroughput();

    HeapSize getHeapSize();

    GCPause getGCPause();

    GCInterval getGCInterval();
}

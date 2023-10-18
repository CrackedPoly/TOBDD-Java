package org.nextlab.tobdd;

import java.util.concurrent.atomic.AtomicInteger;

public class Node {
    public int level;
    public final Node low;
    public final Node high;
    public AtomicInteger refCount = new AtomicInteger(0);

    public Node(int level, Node low, Node high) {
        this.level = level;
        this.low = low;
        this.high = high;
    }

    public Node ref() {
        refCount.incrementAndGet();
        return this;
    }

    public void deref() {
        refCount.decrementAndGet();
    }

    public int refCount() {
        return refCount.get();
    }
}
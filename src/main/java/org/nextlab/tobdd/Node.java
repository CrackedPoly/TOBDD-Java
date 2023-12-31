package org.nextlab.tobdd;

import java.util.concurrent.atomic.AtomicInteger;

// A Node consumes 4+8+8+16=36 bytes on 64-bit JVM.
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

    private String _toString(int tabLevel) {
        if (low == null && high == null) {
            return level == Integer.MAX_VALUE ? "\"FALSE\"" : "\"TRUE\"";
        } else {
            return "{\n" +
                    "  ".repeat(tabLevel) + "\"level\": " + level + ",\n" +
                    "  ".repeat(tabLevel) + "\"low\": " + low._toString(tabLevel+1) + ",\n" +
                    "  ".repeat(tabLevel) + "\"high\": " + high._toString(tabLevel+1) + "\n" +
                    "  ".repeat(tabLevel-1) + "}";
        }
    }

    public String toString() {
        return _toString(1);
    }
}
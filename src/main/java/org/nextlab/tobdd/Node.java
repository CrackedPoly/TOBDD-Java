package org.nextlab.tobdd;

public record Node(int level, Node low, Node high) {}

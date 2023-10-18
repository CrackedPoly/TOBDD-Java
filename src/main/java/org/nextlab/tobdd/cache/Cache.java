package org.nextlab.tobdd.cache;

import org.nextlab.tobdd.Node;
import org.nextlab.tobdd.Operator;

public interface Cache {

    Node getIfPresent(int hash, Node left, Node right, Operator op);

    void put(int hash, Node left, Node right, Operator op, Node value);

    void clear();

    int getSize();
}

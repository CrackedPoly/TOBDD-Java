package org.nextlab.tobdd.cache;

import org.nextlab.tobdd.Node;
import org.nextlab.tobdd.Operator;

public record CacheKey(Node left, Node right, Operator op) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheKey cacheKey = (CacheKey) o;
        if (op != cacheKey.op) return false;
        if (!op.isCommutative() && left == cacheKey.left && right == cacheKey.right) return true;
        return op.isCommutative() &&
                ((left == cacheKey.left && right == cacheKey.right) || (left == cacheKey.right && right == cacheKey.left));
    }
}

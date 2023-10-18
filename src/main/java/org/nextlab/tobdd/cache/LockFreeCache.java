package org.nextlab.tobdd.cache;

import org.nextlab.tobdd.Node;
import org.nextlab.tobdd.Operator;

public class LockFreeCache implements Cache {
    public CacheItem[] _cache;
    public int size;

    public LockFreeCache(int cacheSize) {
        this.size = cacheSize;
        this._cache = new CacheItem[cacheSize];
    }

    public Node getIfPresent(int hash, Node left, Node right, Operator op) {
        CacheItem item = _cache[(hash & 0x7FFFFFFF) % _cache.length];
        if (item == null) {
            return null;
        } else if (item.keyEquals(left, right, op)) {
            return item.value;
        } else {
            return null;
        }
    }

    public void put(int hash, Node left, Node right, Operator op, Node value) {
        _cache[((hash & 0x7FFFFFFF) % _cache.length)] = new CacheItem(left, right, op, value);
    }

    @Override
    public void clear() {
        this._cache = new CacheItem[size];
    }

    @Override
    public int getSize() {
        return size;
    }

    private static class CacheItem {
        private final Node left;
        private final Node right;
        private final Operator op;

        private final Node value;

        public CacheItem(Node left, Node right, Operator op, Node value) {
            this.left = left;
            this.right = right;
            this.op = op;
            this.value = value;
        }

        public boolean keyEquals(Node left, Node right, Operator op) {
            return (this.op == op && this.left == left && this.right == right);
        }
    }
}

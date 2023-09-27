package org.nextlab.tobdd.table;

import org.nextlab.tobdd.*;
import org.nextlab.tobdd.cache.LockFreeCache;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HashBucketTable extends Context {
    private final int tableSize;
    private final Vector<Bucket> table;


    /**
     * Create a new NodeTable with `tableSize` buckets and `cacheSize` cache size.
     */
    public HashBucketTable(int tableSize, int cacheSize, int varNum) {
        super(varNum);
        this.tableSize = tableSize;
        this.table = new Vector<>(tableSize);
        for (int i = 0; i < tableSize; i++) {
            table.add(new Bucket());
        }
        this.cache = new LockFreeCache(cacheSize, this::compute);

        // super() only create these nodes, but not put them into the table.
        getOrCreate(falseNode.level(), falseNode.low(), falseNode.high());
        getOrCreate(trueNode.level(), trueNode.low(), trueNode.high());
        for (Node n: vars) {
            getOrCreate(n.level(), n.low(), n.high());
        }
        for (Node n: nVars) {
            getOrCreate(n.level(), n.low(), n.high());
        }
    }

    public Node getOrCreate(int level, Node low, Node high) {
        if (low == null && high == null) {
            Bucket bucket = table.get(level % tableSize);
            return bucket.getOrCreate(level, null, null);
        }
        assert low != null && high != null;
        int hash = Hash.HASH_O_3(level, low.hashCode(), high.hashCode()) % tableSize;
        Bucket bucket = table.get(Math.abs(hash));
        return bucket.getOrCreate(level, low, high);
    }

    public int getNodeNum() {
        int num = 0;
        for (Bucket bucket : table) {
            num += bucket.size();
        }
        return num;
    }

    private static class Bucket {
        private final ConcurrentLinkedQueue<Node> nodeList;

        public Bucket() {
            this.nodeList = new ConcurrentLinkedQueue<>();
        }

        public Node getOrCreate(int level, Node low, Node high) {
            for (Node node : this.nodeList) {
                if (node.level() == level && node.low() == low && node.high() == high) {
                    return node;
                }
            }
            Node newNode = new Node(level, low, high);
            this.nodeList.add(newNode);
            return newNode;
        }

        public int size() {
            return this.nodeList.size();
        }
    }
}

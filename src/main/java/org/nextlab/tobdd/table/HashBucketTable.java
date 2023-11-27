package org.nextlab.tobdd.table;

import org.nextlab.tobdd.*;
import org.nextlab.tobdd.cache.LockFreeCache;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

/*
 * HashBucketTable stores nodes in a hash table with buckets.
 * */
public class HashBucketTable extends Context {
    private final int tableSize;
    private final Vector<LockFreeBucket> table;
    public static final int NODE_MARK = 0x80000000, NODE_UNMARK = 0x7FFFFFFF;


    /**
     * Create a new NodeTable with `tableSize` buckets and `cacheSize` cache size.
     */
    public HashBucketTable(int tableSize, int cacheSize, int varNum) {
        super(varNum);
        this.tableSize = tableSize;
        this.table = new Vector<>(tableSize);
        for (int i = 0; i < tableSize; i++) {
            table.add(new LockFreeBucket());
        }
        this.opCache = new LockFreeCache(cacheSize);
        this.notCache = new LockFreeCache(cacheSize);

        this.falseNode = getOrCreate(Integer.MAX_VALUE, null, null).ref();
        this.trueNode = getOrCreate(Integer.MAX_VALUE-1, null, null).ref();
        for (int i = 0; i < varNum; i++) {
            Node var = getOrCreate(i, falseNode, trueNode).ref();
            Node nVar = getOrCreate(i, trueNode, falseNode).ref();
            vars.add(var);
            nVars.add(nVar);
        }
    }

    @Override
    public boolean isVar(Node node) {
        return isFalse(node.low) && isTrue(node.high);
    }

    @Override
    public boolean isNVar(Node node) {
        return isTrue(node.low) && isFalse(node.high);
    }

    /**
     * Get or create a node in the hash table.
     */
    @Override
    public Node getOrCreate(int level, Node low, Node high) {
        if (low == null && high == null) {
            LockFreeBucket bucket = table.get(level % tableSize);
            return bucket.getOrCreate(level, null, null);
        }
        assert low != null && high != null;
        int hash = (Hash.HASH_O_3(level, low.hashCode(), high.hashCode()) & 0x7FFFFFFF) % tableSize;
        LockFreeBucket bucket = table.get(hash);
        return bucket.getOrCreate(level, low, high);
    }

    /**
     * Get the number of nodes in the hash table.
     */
    @Override
    public int getNodeNum() {
        int num = 0;
        for (LockFreeBucket bucket : table) {
            num += bucket.size();
        }
        return num;
    }

    /**
     * Garbage collection. After gc() only nodes that are referenced and their descendants will be kept.
     * <p>
     * Warning: Not thread-safe! Call gc() in a single thread.
     */
    @Override
    public void gc(boolean verbose) {
        if (verbose) System.out.println("#Node before gc: " + getNodeNum());
        // mark nodes in use, those nodes and their children will be kept
        for (LockFreeBucket bucket : table) {
            markNodesInUse(bucket);
        }
        // skip unmarked nodes
        for (LockFreeBucket bucket : table) {
            LockFreeBucket.ListNode curr = bucket.atomicHead.get();
            if (curr == null) {
                continue;
            }
            if (curr.next == null && !isMarked(curr.node)) {
                bucket.atomicHead.compareAndSet(curr, null);
                continue;
            }
            while (curr.next != null) {
                if (!isMarked(curr.next.node)) curr.next = curr.next.next;
                else curr = curr.next;
            }
        }
        // unmark all nodes
        for (LockFreeBucket bucket : table) {
            unmarkNodes(bucket);
        }
        opCache.clear();
        notCache.clear();
        if (verbose) System.out.println("#Node after gc: " + getNodeNum());
    }

    private void markNodesInUse(LockFreeBucket bucket) {
        LockFreeBucket.ListNode curr = bucket.atomicHead.get();
        while (curr != null) {
            if (curr.node.refCount() > 0) {
                markNodeRec(curr.node);
            }
            curr = curr.next;
        }
    }

    private void unmarkNodes(LockFreeBucket bucket) {
        LockFreeBucket.ListNode curr = bucket.atomicHead.get();
        while (curr != null) {
            unmarkNodeRec(curr.node);
            curr = curr.next;
        }
    }

    private boolean isMarked(Node node) {
        return (node.level & NODE_MARK) != 0;
    }

    private void markNodeRec(Node node) {
        if (node == null) return;
        if (!isMarked(node)) {
            node.level |= NODE_MARK;
            markNodeRec(node.low);
            markNodeRec(node.high);
        }
    }

    private void unmarkNodeRec(Node node) {
        if (node == null) return;
        if (isMarked(node)) {
            node.level &= NODE_UNMARK;
            unmarkNodeRec(node.low);
            unmarkNodeRec(node.high);
        }
    }

    private static class LockFreeBucket {
        private static class ListNode {
            public Node node;
            public ListNode next;
        }

        public final AtomicReference<ListNode> atomicHead;

        public LockFreeBucket() {
            this.atomicHead = new AtomicReference<>(null);
        }

        public Node getOrCreate(int level, Node low, Node high) {
            ListNode oldHead = atomicHead.get();
            for (ListNode curr = oldHead; curr != null; curr = curr.next) {
                if (curr.node.level == level && curr.node.low == low && curr.node.high == high) {
                    return curr.node;
                }
            }
            ListNode newListNode = new ListNode();
            newListNode.node = new Node(level, low, high);
            newListNode.next = oldHead;
            ListNode newHead;
            while (true) {
                newHead = atomicHead.compareAndExchange(newListNode.next, newListNode);
                if (newHead == newListNode.next) {
                    break;
                }
                for (ListNode curr = newHead; curr != newListNode.next; curr = curr.next) {
                    if (curr.node.level == level && curr.node.low == low && curr.node.high == high) {
                        return curr.node;
                    }
                }
                newListNode.next = newHead;
            }

            return newListNode.node;
        }

        public int size() {
            int count = 0;
            ListNode curr = atomicHead.get();
            while (curr != null) {
                count++;
                curr = curr.next;
            }
            return count;
        }
    }
}

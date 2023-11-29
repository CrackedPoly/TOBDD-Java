package org.nextlab.tobdd.table;

import org.nextlab.tobdd.*;
import org.nextlab.tobdd.cache.Cache;
import java.util.Vector;

/*
 * Context implements the basic logic operations on BDDs.
 * Classes that extend Context varies in how they store the nodes.
 * */
public abstract class Context {
    protected Cache opCache;
    protected Cache notCache;
    protected Vector<Node> vars;
    protected Vector<Node> nVars;
    protected Node falseNode;
    protected Node trueNode;

    public Context(int varNum) {
        this.vars = new Vector<>(varNum);
        this.nVars = new Vector<>(varNum);
    }

    /*
    * Get an existing variable node by id.
    * */
    public Node getVar(int id) {
        return vars.get(id);
    }

    /*
     * Get an existing negative variable node by id.
     * */
    public Node getNVar(int id) {
        return nVars.get(id);
    }

    /*
    * Get the true node.
    * */
    public Node getTrue() {
        return trueNode;
    }

    /*
     * Get the false node.
     * */
    public Node getFalse() {
        return falseNode;
    }

    /*
     * Check if a node is the true node.
     * */
    public boolean isTrue(Node node) {
        return node == trueNode;
    }

    /*
     * Check if a node is the false node.
     * */
    public boolean isFalse(Node node) {
        return node == falseNode;
    }

    public abstract boolean isVar(Node node);

    public abstract boolean isNVar(Node node);

    /*
     * left & right
     * */
    public Node bddAnd(Node left, Node right) {
        if (left == right) {
            return left;
        }
        if (isFalse(left) || isFalse(right)) {
            return falseNode;
        }
        if (isTrue(left)) {
            return right;
        }
        if (isTrue(right)) {
            return left;
        }
        Node tmp = left;
        if (right.level > left.level) {
            left = right;
            right = tmp;
        }
        int hash = Hash.HASH_O_3(left.hashCode(), right.hashCode(), Operator.AND.ordinal());
        tmp = opCache.getIfPresent(hash, left, right, Operator.AND);
        if (tmp != null) {
            return tmp;
        }
        int m = right.level;
        Node fLow = bddAnd(negCof(left, m), negCof(right, m));
        Node fHigh = bddAnd(posCof(left, m), posCof(right, m));
        Node result = combine(m, fLow, fHigh);
        opCache.put(hash, left, right, Operator.AND, result);
        return result;
    }

    /*
     * left | right
     * */
    public Node bddOr(Node left, Node right) {
        if (left == right) {
            return left;
        }
        if (isTrue(left) || isTrue(right)) {
            return trueNode;
        }
        if (isFalse(left)) {
            return right;
        }
        if (isFalse(right)) {
            return left;
        }
        Node tmp = left;
        if (right.level > left.level) {
            left = right;
            right = tmp;
        }
        int hash = Hash.HASH_O_3(left.hashCode(), right.hashCode(), Operator.OR.ordinal());
        tmp = opCache.getIfPresent(hash, left, right, Operator.OR);
        if (tmp != null) {
            return tmp;
        }
        int m = right.level;
        Node fLow = bddOr(negCof(left, m), negCof(right, m));
        Node fHigh = bddOr(posCof(left, m), posCof(right, m));
        Node result = combine(m, fLow, fHigh);
        opCache.put(hash, left, right, Operator.OR, result);
        return result;
    }

    /*
     * !node
     * */
    public Node bddNot(Node node) {
        if (isTrue(node)) {
            return falseNode;
        }
        if (isFalse(node)) {
            return trueNode;
        }
        Node tmp = notCache.getIfPresent(node.hashCode(), node, node, Operator.NOT);
        if (tmp != null) {
            return tmp;
        }
        int m = node.level;
        Node fLow = bddNot(negCof(node, m));
        Node fHigh = bddNot(posCof(node, m));
        Node result = combine(m, fLow, fHigh);
        notCache.put(node.hashCode(), node, node, Operator.NOT, result);
        return result;
    }

    /*
     * left ^ right
     * */
    public Node bddXor(Node left, Node right) {
        if (left == right) {
            return falseNode;
        }
        if (isTrue(left)) {
            return bddNot(right);
        }
        if (isFalse(left)) {
            return right;
        }
        if (isTrue(right)) {
            return bddNot(left);
        }
        if (isFalse(right)) {
            return left;
        }
        Node tmp = left;
        if (right.level > left.level) {
            left = right;
            right = tmp;
        }
        int hash = Hash.HASH_O_3(left.hashCode(), right.hashCode(), Operator.XOR.ordinal());
        tmp = opCache.getIfPresent(hash, left, right, Operator.XOR);
        if (tmp != null) {
            return tmp;
        }
        int m = right.level;
        Node fLow = bddXor(negCof(left, m), negCof(right, m));
        Node fHigh = bddXor(posCof(left, m), posCof(right, m));
        Node result = combine(m, fLow, fHigh);
        opCache.put(hash, left, right, Operator.XOR, result);
        return result;
    }

    /*
     * left - right
     * */
    public Node bddDiff(Node left, Node right) {
        if (left == right || isFalse(left) || isTrue(right)) {
            return falseNode;
        }
        if (isFalse(right)) {
            return left;
        }
        int hash = Hash.HASH_O_3(left.hashCode(), right.hashCode(), Operator.DIFF.ordinal());
        Node tmp = opCache.getIfPresent(hash, left, right, Operator.DIFF);
        if (tmp != null) {
            return tmp;
        }
        int m = Math.min(left.level, right.level);
        Node fLow = bddDiff(negCof(left, m), negCof(right, m));
        Node fHigh = bddDiff(posCof(left, m), posCof(right, m));
        Node result = combine(m, fLow, fHigh);
        opCache.put(hash, left, right, Operator.DIFF, result);
        return result;
    }

    /*
     * left -> right
     * */
    public Node bddImp(Node left, Node right) {
        if (isFalse(left) || isTrue(right)) {
            return trueNode;
        }
        if (isTrue(left)) {
            return right;
        }
        int hash = Hash.HASH_O_3(left.hashCode(), right.hashCode(), Operator.IMP.ordinal());
        Node tmp = opCache.getIfPresent(hash, left, right, Operator.IMP);
        if (tmp != null) {
            return tmp;
        }
        int m = Math.min(left.level, right.level);
        Node fLow = bddImp(negCof(left, m), negCof(right, m));
        Node fHigh = bddImp(posCof(left, m), posCof(right, m));
        Node result = combine(m, fLow, fHigh);
        opCache.put(hash, left, right, Operator.IMP, result);
        return result;
    }

    /*
    * return true if two bdds' conjunction is empty
    * */
    public Boolean bddAndEmpty(Node left, Node right) {
        if (isFalse(left) || isFalse(right)) {
            return true;
        }
        if (isTrue(left) || isTrue(right)) {
            return false;
        }
        if (left == right) {
            return false;
        }
        if (left.level == right.level) {
            return bddAndEmpty(left.low, right.low) && bddAndEmpty(left.high, right.high);
        }
        if ((isVar(left) || isNVar(left)) && (isVar(right) || isNVar(right))) {
            return false;
        }
        // now left and right are both bdds with different level
        Node tmp = left;
        if (right.level > left.level) {
            left = right;
            right = tmp;
        }
        if (isTrue(right.low) || isTrue(right.high)) {
            return false;
        } else {
            return bddAndEmpty(left, right.low) && bddAndEmpty(left, right.high);
        }
    }

    private Node negCof(Node x, int id) {
        if (id < x.level) return x;
        return x.low;
    }

    private Node posCof(Node x, int id) {
        if (id < x.level) return x;
        return x.high;
    }

    private Node combine(int level, Node low, Node high) {
        if (low == high) return low;
        return getOrCreate(level, low, high);
    }

    /*
     * Get or create a node from the context.
     * */
    public abstract Node getOrCreate(int level, Node low, Node high);

    /*
     * Get the number of nodes in the context.
     * */
    public abstract int getNodeNum();

    public abstract void gc(boolean verbose);

    public abstract Node fromJson(String jsonStr);
}

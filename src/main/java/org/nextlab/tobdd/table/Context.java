package org.nextlab.tobdd.table;

import org.nextlab.tobdd.*;
import org.nextlab.tobdd.cache.Cache;
import org.nextlab.tobdd.cache.CacheKey;
import java.util.Vector;

public abstract class Context {
    protected Cache cache;
    protected Vector<Node> vars;
    protected Vector<Node> nVars;
    protected Node falseNode;
    protected Node trueNode;

    public Context(int varNum) {
        this.vars = new Vector<>(varNum);
        this.nVars = new Vector<>(varNum);
        this.falseNode = new Node(Integer.MAX_VALUE, null, null);
        this.trueNode = new Node(Integer.MAX_VALUE-1, null, null);
        for (int i = 0; i < varNum; i++) {
            Node var = new Node(i, falseNode, trueNode);
            Node nVar = new Node(i, trueNode, falseNode);
            vars.add(var);
            nVars.add(nVar);
        }
    }

    public Node getVar(int id) {
        return vars.get(id);
    }

    public Node getNVar(int id) {
        return nVars.get(id);
    }

    public Node getTrue() {
        return trueNode;
    }

    public Node getFalse() {
        return falseNode;
    }

    public boolean isTrue(Node node) {
        return node == trueNode;
    }

    public boolean isFalse(Node node) {
        return node == falseNode;
    }

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
        return cache.getOrCompute(new CacheKey(left, right, Operator.AND));
    }

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
        return cache.getOrCompute(new CacheKey(left, right, Operator.OR));
    }

    public Node bddNot(Node node) {
        if (isTrue(node)) {
            return falseNode;
        }
        if (isFalse(node)) {
            return trueNode;
        }
        return cache.getOrCompute(new CacheKey(node, null, Operator.NOT));
    }

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
        return cache.getOrCompute(new CacheKey(left, right, Operator.XOR));
    }

    public Node bddDiff(Node left, Node right) {
        if (left == right || isFalse(left) || isTrue(right)) {
            return falseNode;
        }
        if (isFalse(right)) {
            return left;
        }
        return cache.getOrCompute(new CacheKey(left, right, Operator.DIFF));
    }

    public Node bddImp(Node left, Node right) {
        if (isFalse(left) || isTrue(right)) {
            return trueNode;
        }
        if (isTrue(left)) {
            return right;
        }
        return cache.getOrCompute(new CacheKey(left, right, Operator.IMP));
    }

    public Node negCof(Node x, int id) {
        if (id < x.level()) return x;
        return x.low();
    }

    public Node posCof(Node x, int id) {
        if (id < x.level()) return x;
        return x.high();
    }

    public Node combine(int level, Node low, Node high) {
        if (low == high) return low;
        return getOrCreate(level, low, high);
    }

    /**
     * Get or create a node with `level`, `low` and `high` from existing nodes.
     */
    public abstract Node getOrCreate(int level, Node low, Node high);

    public abstract int getNodeNum();

    protected Node compute(CacheKey key) {
        Node left = key.left();
        Node right = key.right();
        Operator op = key.op();
        if (op == Operator.AND) {
            int m = Math.min(left.level(), right.level());
            Node fLow = bddAnd(negCof(left, m), negCof(right, m));
            Node fHigh = bddAnd(posCof(left, m), posCof(right, m));
            return combine(m, fLow, fHigh);
        } else if (op == Operator.OR) {
            int m = Math.min(left.level(), right.level());
            Node fLow = bddOr(negCof(left, m), negCof(right, m));
            Node fHigh = bddOr(posCof(left, m), posCof(right, m));
            return combine(m, fLow, fHigh);
        } else if (op == Operator.NOT) {
            int m = left.level();
            Node fLow = bddNot(negCof(left, m));
            Node fHigh = bddNot(posCof(left, m));
            return combine(m, fLow, fHigh);
        } else if (op == Operator.XOR) {
            int m = Math.min(left.level(), right.level());
            Node fLow = bddXor(negCof(left, m), negCof(right, m));
            Node fHigh = bddXor(posCof(left, m), posCof(right, m));
            return combine(m, fLow, fHigh);
        } else if (op == Operator.DIFF) {
            int m = Math.min(left.level(), right.level());
            Node fLow = bddDiff(negCof(left, m), negCof(right, m));
            Node fHigh = bddDiff(posCof(left, m), posCof(right, m));
            return combine(m, fLow, fHigh);
        } else if (op == Operator.IMP) {
            int m = Math.min(left.level(), right.level());
            Node fLow = bddImp(negCof(left, m), negCof(right, m));
            Node fHigh = bddImp(posCof(left, m), posCof(right, m));
            return combine(m, fLow, fHigh);
        } else {
            throw new RuntimeException("Unsupported BDD operation.");
        }
    }
}

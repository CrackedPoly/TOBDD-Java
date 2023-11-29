package org.nextlab.tobdd;

import org.nextlab.tobdd.table.HashBucketTable;
import org.nextlab.tobdd.table.Context;

import java.math.BigInteger;

public class TOBDD {
    private final Context nodeContext;
    private final int varNum;

    public TOBDD(int tableSize, int cacheSize, int varNum) {
        BigInteger tS = new BigInteger(String.valueOf(tableSize));
        int _tableSize = tS.nextProbablePrime().intValue();
        BigInteger cS = new BigInteger(String.valueOf(cacheSize));
        int _cacheSize = cS.nextProbablePrime().intValue();
        this.nodeContext = new HashBucketTable(_tableSize, _cacheSize, varNum);
        this.varNum = varNum;
    }

    public Node getVar(int id) {
        assert id < varNum;
        return nodeContext.getVar(id);
    }

    public Node getNVar(int id) {
        assert id < varNum;
        return nodeContext.getNVar(id);
    }

    public Node getTrue() {
        return nodeContext.getTrue();
    }

    public Node getFalse() {
        return nodeContext.getFalse();
    }

    public Node And(Node left, Node right) {
        return nodeContext.bddAnd(left, right);
    }

    public Node Or(Node left, Node right) {
        return nodeContext.bddOr(left, right);
    }

    public Node Not(Node node) {
        return nodeContext.bddNot(node);
    }

    public Boolean AndAny(Node left, Node right) {
        return !nodeContext.bddAndEmpty(left, right);
    }

    public Node Xor(Node left, Node right) {
        return nodeContext.bddXor(left, right);
    }

    public Node Diff(Node left, Node right) {
        return nodeContext.bddDiff(left, right);
    }

    public Node Imp(Node left, Node right) {
        return nodeContext.bddImp(left, right);
    }

    public Node fromJson(String jsonStr) {
        return nodeContext.fromJson(jsonStr);
    }

    public void gc(boolean verbose) {
        nodeContext.gc(verbose);
    }

    public int getNodeNum() {
        return nodeContext.getNodeNum();
    }
}

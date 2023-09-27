package org.nextlab.tobdd;

import org.nextlab.tobdd.table.HashBucketTable;
import org.nextlab.tobdd.table.Context;

public class TOBDD {
    private final Context nodeContext;
    private final int varNum;

    public TOBDD(int tableSize, int cacheSize, int varNum) {
        this.nodeContext = new HashBucketTable(tableSize, cacheSize, varNum);
        this.varNum = varNum;
    }

    public BDD getVar(int id) {
        assert id < varNum;
        return new BDD(nodeContext.getVar(id), nodeContext);
    }

    public BDD getNVar(int id) {
        assert id < varNum;
        return new BDD(nodeContext.getNVar(id), nodeContext);
    }

    public BDD getTrue() {
        return new BDD(nodeContext.getTrue(), nodeContext);
    }

    public BDD getFalse() {
        return new BDD(nodeContext.getFalse(), nodeContext);
    }

    public int getNodeNum() {
        return nodeContext.getNodeNum();
    }
}

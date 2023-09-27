package org.nextlab.tobdd;

import org.nextlab.tobdd.table.Context;
import java.util.Objects;

public class BDD {
    private final Node root;
    private final Context context;

    public BDD(Node root, Context context) {
        this.root = root;
        this.context = context;
    }

    public Node getRoot() {
        return root;
    }

    public boolean isFalse() {
        return context.isFalse(root);
    }

    public boolean isTrue() {
        return context.isTrue(root);
    }

    public BDD and(BDD bdd) {
        return new BDD(context.bddAnd(this.root, bdd.getRoot()), context);
    }

    public BDD or(BDD bdd) {
        return new BDD(context.bddOr(this.root, bdd.getRoot()), context);
    }

    public BDD not() {
        return new BDD(context.bddNot(this.root), context);
    }

    public BDD xor(BDD bdd) {
        return new BDD(context.bddXor(this.root, bdd.getRoot()), context);
    }

    public BDD diff(BDD bdd) {
        return new BDD(context.bddDiff(this.root, bdd.getRoot()), context);
    }

    public BDD imp(BDD bdd) {
        return new BDD(context.bddImp(this.root, bdd.getRoot()), context);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BDD bdd)) return false;
        return Objects.equals(root, bdd.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }
}

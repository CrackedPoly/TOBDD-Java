package org.nextlab.tobdd;

public enum Operator {
    AND, OR, NOT, XOR, DIFF, IMP;

    public boolean isCommutative() {
        return this == AND || this == OR || this == NOT || this == XOR;
    }
}

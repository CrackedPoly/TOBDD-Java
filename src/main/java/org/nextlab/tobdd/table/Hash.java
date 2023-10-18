package org.nextlab.tobdd.table;

/**
 * Hash helper functions using prime multiplications.
 */
public class Hash {

    // prime numbers are stolen from CUDD
    private static final int DD_P1 = 12582917, DD_P2 = 4256249, DD_P3 = 741457, DD_P4 = 1618033999;

    public static int HASH_O_2(int a, int b) {
        return (a*DD_P1) + (b*DD_P2);
    }

    public static int HASH_O_3(int a, int b, int c) {
        return (a*DD_P1) + (b*DD_P2) + (c*DD_P3);
    }
}


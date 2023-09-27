package org.nextlab.tobdd.table;

public class Hash {
    public static int HASH_O_2(int a, int b) {
        return (a+b)*(a+b+1)>>1 + a;
    }

    public static int HASH_UO_2(int a, int b) {
        return (a+b)*(a+b+1)>>1 + 1;
    }

    public static int HASH_O_3(int a, int b, int c) {
        return HASH_O_2(c, HASH_O_2(a, b));
    }

    public static int HASH_UO_3(int a, int b, int c) {
        return HASH_UO_2(c, HASH_UO_2(a, b));
    }

    public static int HASH_UO_O_3(int a, int b, int c) {
        return HASH_O_2(c, HASH_UO_2(a, b));
    }
}


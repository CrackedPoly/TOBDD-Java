package org.nextlab;

import org.nextlab.tobdd.Node;
import org.nextlab.tobdd.TOBDD;

public class Main {
    public static void main(String[] args) {
        System.out.println("TOBDD-simple example:");
        TOBDD tobdd = new TOBDD(1000, 1000, 4);

        Node b1111 = tobdd.And(
            tobdd.getVar(0), tobdd.And(
                tobdd.getVar(1), tobdd.And(
                    tobdd.getVar(2), tobdd.getVar(3)
                )
            )
        );

        Node b1xxx = tobdd.And(
            tobdd.getVar(0), tobdd.And(
                tobdd.getTrue(), tobdd.And(
                    tobdd.getTrue(), tobdd.getTrue()
                )
            )
        );

        Node b0xxx = tobdd.And(
            tobdd.getNVar(0), tobdd.And(
                tobdd.getTrue(), tobdd.And(
                    tobdd.getTrue(), tobdd.getTrue()
                )
            )
        );

        Node bxx11 = tobdd.And(
            tobdd.getTrue(), tobdd.And(
                tobdd.getTrue(), tobdd.And(
                    tobdd.getVar(2), tobdd.getVar(3)
                )
            )
        );

        Node bxxx1 = tobdd.And(
            tobdd.getTrue(), tobdd.And(
                tobdd.getTrue(), tobdd.And(
                    tobdd.getTrue(), tobdd.getVar(3)
                )
            )
        );


        System.out.println("b1111 and bxx11 should be: " + (tobdd.AndAny(b1111, bxx11)));
        System.out.println("b1111 and bxxx1 should be: " + (tobdd.AndAny(b1111, bxxx1)));
        System.out.println("b1xxx and b0xxx should be: " + (tobdd.AndAny(b1xxx, b0xxx)));
        System.out.println("b1xxx and b1111 should be: " + (tobdd.AndAny(b1xxx, b1111)));
        System.out.println("b1xxx and bxx11 should be: " + (tobdd.AndAny(b1xxx, bxx11)));
        System.out.println("b1xxx and bxxx1 should be: " + (tobdd.AndAny(b1xxx, bxxx1)));
        System.out.println("b0xxx and b1111 should be: " + (tobdd.AndAny(b0xxx, b1111)));
        System.out.println("b0xxx and bxx11 should be: " + (tobdd.AndAny(b0xxx, bxx11)));
        System.out.println("b0xxx and bxxx1 should be: " + (tobdd.AndAny(b0xxx, bxxx1)));
    }
}
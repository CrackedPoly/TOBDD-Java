package org.nextlab;

import org.nextlab.tobdd.BDD;
import org.nextlab.tobdd.TOBDD;

public class Main {
    public static void main(String[] args) {
        System.out.println("TOBDD-simple example:");
        TOBDD tobdd = new TOBDD(1000, 1000, 3);

        BDD x = tobdd.getVar(0);
        BDD y = tobdd.getVar(1);
        BDD z = tobdd.getVar(2);

        BDD xy = x.and(y);
        BDD xyz = xy.and(z);
        BDD xyZ = xy.and(z.not());

        System.out.println("Node Count: " + tobdd.getNodeNum());

        assert xy.equals(xyz.or(xyZ)): "Error: xy != xyz.or(xyZ)";
        assert !xy.equals(tobdd.getFalse()): "Error: xy == FALSE";
    }
}
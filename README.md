# Throughput-Optimized BDD (TOBDD) Library
This is a Java port of [TOBDD](https://github.com/guodong/tobdd).

## Installation
Install to local maven repository:
```bash
$ mvn install
```
Then you are ready to use it in your maven project:
```xml
<dependency>
    <groupId>org.nextlab</groupId>
    <artifactId>tobdd</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage
A simple example is as follows:
```java
package org.nextlab;

import org.nextlab.tobdd.Node;
import org.nextlab.tobdd.TOBDD;

public class Main {
    public static void main(String[] args) {
        System.out.println("TOBDD-simple example:");
        TOBDD tobdd = new TOBDD(1000, 1000, 3);

        Node x = tobdd.getVar(2);
        Node y = tobdd.getVar(1);
        Node z = tobdd.getVar(0);

        Node xy = tobdd.And(x, y).ref();
        Node xyz = tobdd.And(xy, z).ref();
        Node xyZ = tobdd.And(xy, tobdd.Not(z)).ref();

        assert xy == tobdd.Or(xyz, xyZ): "Error: xy != xyz.or(xyZ)";
        assert !(xy == tobdd.getFalse()): "Error: xy == FALSE";

        // Because xy is used by xyz and xyZ, by de-referencing xy, xy will not be garbage collected.
        xy.deref();
        tobdd.gc(true);
        // De-ref xyz and xyZ, now all of them will be garbage collected.
        xyz.deref();
        xyZ.deref();
        tobdd.gc(true);
    }
}
```
## License
This project is licensed under Apache License 2.0.
package io.gamioo.sandbox;

import org.openjdk.jol.info.GraphLayout;

public class JavaObjectLayout {



    static void main() {

        Point point=new Point();
        point.setX(1);
        point.setY(2);
;

        long total = GraphLayout.parseInstance(new Integer(1)).totalSize();
        System.out.println(total);

        int i=85966080;

        System.out.println(Integer.toBinaryString(i));


    }

}

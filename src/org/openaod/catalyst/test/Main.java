package org.openaod.catalyst.test;

import org.openaod.catalyst.Controller;

public class Main {

    public static void main(String[] args) {
        FP1 fp1 = new FP1();
        FP2 fp2 = new FP2();
        FP3 fp3 = new FP3();
        Controller.addProcess(fp1);
        Controller.addProcess(fp2);
        Controller.addProcess(fp3);
        Controller.run();
    }

}

package org.openaod.catalyst.test;

import org.openaod.catalyst.Process;

public class FP1 extends Process {

    @Override
    public void run() {
        System.out.println("Fact1: Processing ...");
        int size  = 50;
        int multiplier = 6;

        double n[][][] = new double[size][size][size];
        double m[] = new double[multiplier];

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                for(int k = 0; k < size; k++) {
                    n[i][j][k] = Math.random();
                }
            }
        }

        for(int i = 0; i < multiplier; i++) m[i] = 100 * Math.random() - 100;

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                for(int k = 0; k < size; k++) {
                    int sum = 0;
                    int sum2 = 0;
                    for(int k2 = 0; k2 < size; k2++) {
                        for(int k3 = 0; k3 < size; k3++) {
                            sum += n[i][j][k] * (n[i][k2][k3] - n[k2][i][k3] + n[k3][k2][i]);
                        }
                    }
                    for(int z=0; z < multiplier; z++) {
                        for(int k2 = 0; k2 < size; k2++) {
                            for(int k3 = 0; k3 < size; k3++) {
                                sum2 += m[z] * (n[i][k2][k3] - n[k2][i][k3] + n[k3][k2][i]);
                            }
                        }
                    }
                    n[i][j][k] = Math.pow(Math.sin(sum2 - sum),Math.random()*9);
                }
            }
        }
        System.out.println("Fact1: Done ...");
    }

    @Override
    public String getName() {
        return "Fact1";
    }

    @Override
    public String[] getDependencies() {
        return null;
    }
}

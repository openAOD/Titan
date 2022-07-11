package org.openaod.titan.catalyst;

import org.openaod.catalyst.ConsoleHandle;
import org.openaod.catalyst.Process;

import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    private final static Vector<Process> processVector = new Vector<>(1,1);

    private final static ConsoleHandle csh = new ConsoleHandle();

    public static void addProcess(Process p) {
        if(search(p.getName()) != -1) {
            csh.println("Catalyst: Process \""+p.getName()+"\" has a duplicate name. Please use a non-duplicate Process Identification Name");
            System.exit(100);
        }
        processVector.addElement(p);
    }

    public static void run() {
        csh.println("Catalyst version 0.1-beta-1");
        csh.println("Licensed under Apache License 2.0");
        csh.println("Copyright (C) 2022, openAOD");
        csh.println();
        csh.println("Catalyst: Current Process Stack Size: " + processVector.size());
        csh.println("Catalyst: Multithreading Enabled: "+Configuration.useMultithreading);
        csh.println("Catalyst: Detected Processor Cores: "+Runtime.getRuntime().availableProcessors());
        csh.println("Catalyst: Host Operating System: "+System.getProperty("os.name"));
        csh.println();
        csh.println("Catalyst: Verifying Processes ... ");
        for(int i=0;i<processVector.size();i++) {
            Process p = processVector.elementAt(i);
            if(p.isVerified) {
                csh.println("Catalyst: Verifying Process ["+(i+1)+"/"+processVector.size()+"] ... already verified, skipping");
                continue;
            }
            csh.println("Catalyst: Verifying Process ["+(i+1)+"/"+processVector.size()+"] ... ");
            String[] deps = p.getDependencies();
            csh.println("    Name: "+p.getName());
            if(deps == null) {
                csh.println("    Defined Dependencies: None");
                continue;
            }
            csh.println("    Defined Dependencies: "+deps.length);
            for(int j=0; j<deps.length; j++) {
                csh.print("        Dependency ["+(j+1)+"/"+deps.length+"] "+deps[j]+" : ");
                if(search(deps[j]) == -1) {
                    csh.println("Unsatisfied Dependency: Current process stack does not contain process definition for \""+deps[j]+"\". You might have missed adding a process to a process stack");
                    System.exit(101);
                }
                int ret = verifyDependency(p, processVector.elementAt(search(deps[j])));
                if(ret == 0) csh.println("Verified");
                else if(ret == 8) {
                    csh.println("Circular Dependency: Dependency depends on itself.");
                    System.exit(102);
                } else if(ret == 9) {
                    csh.println("Circular Dependency Detected. Dependencies cannot be resolved.");
                    System.exit(103);
                }
            }
            p.completeVerification();
        }

        while(!isAllComplete()) {
            csh.println("Catalyst: Preparing Execution Stack ... ");
            Vector<Process> executionStack = getExecutionStack();
            csh.println("Catalyst: Execution Stack Prepared. "+executionStack.size()+" out of "+(Configuration.batchSize==-1?"Infinite":Configuration.batchSize)+" available batch units allocated.");
            for(int i=0;i<executionStack.size();i++) csh.println("    Execution Stack ["+(i+1)+"/"+executionStack.size()+"] : Process \""+executionStack.elementAt(i).getName()+"\"");
            int cores = 1;
            if(Configuration.useMultithreading) cores = Runtime.getRuntime().availableProcessors();
            final ExecutorService threads = Executors.newFixedThreadPool(cores);
            try {
                final CountDownLatch latch = new CountDownLatch(executionStack.size());
                for (final Process task : executionStack)
                    threads.execute(() -> {
                        try {
                            task.execute();
                        } finally {
                            latch.countDown();
                        }
                    });
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                threads.shutdown();
            }
            System.gc();
        }

        csh.println("Catalyst: Execution of Processes Complete");
    }

    private static int search(String s) {
        for(int j = 0; j < processVector.size(); j++) if(processVector.elementAt(j).getName().equals(s)) return j;
        return -1;
    }

    private static int verifyDependency(Process root, Process dependency) {
        String deps[] = root.getDependencies();
        boolean b = dependency.getName().equals(root.getName());
        if(b) return 9;
        if(deps == null) return 0;
        for(String d:deps) if(d.equals(root.getName())) return 8;
        deps = dependency.getDependencies();
        if(deps == null) return 0;
        for(String d:deps) {
            Process p = processVector.elementAt(search(d));
            return verifyDependency(root, p);
        }
        return 0;
    }

    private static Vector<Process> getExecutionStack() {
        Vector<Process> executionStack = new Vector<>(1,1);
        for(Process p : processVector) {
            if(p.hasRun) continue;
            else if(hasRequiredDependencies(p)) executionStack.addElement(p);
            if(Configuration.batchSize == -1) continue;
            if(executionStack.size() >= Configuration.batchSize) break;
        }
        return executionStack;
    }

    private static boolean hasRequiredDependencies(Process p) {
        String[] deps = p.getDependencies();
        if(deps == null) return true;
        for(String d:deps) {
            Process dep = processVector.elementAt(search(d));
            if(!dep.hasRun) return false;
        }
        return true;
    }

    private static boolean isAllComplete() {
        for(Process p:processVector) {
            if(!p.hasRun) return false;
        }
        return true;
    }

}

package org.openaod.catalyst;

public abstract class Process {

    boolean hasRun = false;

    boolean isVerified = false;

    void execute() {
        run();
        hasRun = true;
    }

    public abstract void run();

    public abstract String getName();

    public abstract String[] getDependencies();

    final void completeVerification() {
        isVerified = true;
    }

}

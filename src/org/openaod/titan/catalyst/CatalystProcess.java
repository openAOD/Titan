package org.openaod.titan.catalyst;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CatalystProcess {

    protected Logger logger;
    protected String processName = null;
    protected CatalystStack parentStack = null;
    protected CatalystInterface processInterface = new CatalystInterface("ProvisionalCatalystProcessInterface");

    private boolean isRunnable = false;
    private boolean isVerified = false;

    public CatalystStack getParentStack() {
        return parentStack;
    }

    public void setParentStack(CatalystStack parentStack) {
        this.parentStack = parentStack;
    }

    public void setProcessName(String name) {
        processName = name;
    }

    public String getProcessName() {
        return processName;
    }

    public void init() {
        if(!isVerified) verifyProcess();
        logger = Logger.getLogger("CatalystProcess::"+processName);
        processInterface = new CatalystInterface("CatalystProcess::"+processName);
    }

    public abstract void run();

    public final void execute() {
        if(verifyInterface()) {
            logger.log(Level.SEVERE, "Process execution cannot continue due to erred status code in interface");
            CatalystExecutionHandler.terminate(processInterface.getStatusCode());
        }
    }

    public void verifyProcess() {
        if(processName != null && parentStack != null) {
            isVerified = true;
        } else {
            logger.log(Level.SEVERE, "Process cannot be verified due to uninitialized name of process and/or parent stack");
            processInterface.registerStatusCode(101);
        }
    }

    private boolean verifyInterface() {
        return processInterface.getStatusCode() != 0;
    }

}

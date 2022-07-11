package org.openaod.titan.catalyst;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CatalystStack {

    private final Logger logger;
    private final String stackName;
    private final Vector<CatalystProcess> stack;
    private final CatalystInterface catalystInterface;

    public CatalystStack(String name) {
        logger = Logger.getLogger("CatalystStack::"+name);
        stackName = name;
        stack = new Vector<>(1,1);
        catalystInterface = new CatalystInterface("CatalystStack::"+name);
    }

    public void addProcess(CatalystProcess process) {
        process.verifyProcess();
        if(verifyInterface()) {
            logger.log(Level.SEVERE, "Verification of stack cannot continue due to erred verification status code in CatalystInterface");
            CatalystExecutionHandler.terminate(catalystInterface.getStatusCode());
        }
        if(isNotUnique(process)) {
            catalystInterface.registerStatusCode(201);
            logger.log(Level.SEVERE, "Duplicate process names detected. CatalystStack cannot add a duplicate process with the same name");
            CatalystExecutionHandler.terminate(catalystInterface.getStatusCode());
        }
        process.setParentStack(this);
        stack.addElement(process);
    }

    private boolean isNotUnique(CatalystProcess process) {
        for(CatalystProcess p : stack) if(p.getProcessName().equals(process.getProcessName())) return true;
        return false;
    }

    public void verifyStack() {
        for(CatalystProcess p : stack) {
            p.verifyProcess();
            if(verifyInterface()) {
                logger.log(Level.SEVERE, "Verification of stack cannot continue due to erred verification status code in CatalystInterface");
                CatalystExecutionHandler.terminate(catalystInterface.getStatusCode());
            }
        }
    }

    public void initStack() {
        verifyStack();
        for(CatalystProcess p:stack) {
            p.init();
        }
    }

    private boolean verifyInterface() {
        return catalystInterface.getStatusCode() != 0;
    }


}

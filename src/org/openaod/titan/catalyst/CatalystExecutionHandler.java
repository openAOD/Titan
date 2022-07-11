package org.openaod.titan.catalyst;

import org.openaod.titan.util.GlobalExecutionHandler;

public class CatalystExecutionHandler {

    public static void terminate(int status) {
        GlobalExecutionHandler.terminate(status);
    }

}

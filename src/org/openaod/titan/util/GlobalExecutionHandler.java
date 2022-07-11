package org.openaod.titan.util;

import org.openaod.titan.testing.TitanTestingInterface;

public class GlobalExecutionHandler {

    private static TitanTestingInterface tti = null;

    public static void terminate(int status) {
        if(tti != null) {
            if(tti.overrideExitCall) return;
        }

        System.exit(status);
    }

}

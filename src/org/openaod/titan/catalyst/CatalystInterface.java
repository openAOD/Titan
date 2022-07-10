package org.openaod.titan.catalyst;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CatalystInterface {

    private static int statusCode = 0;
    private static final Logger logger = Logger.getLogger("CatalystInterface");
    private final String className;

    public CatalystInterface(String ref) {
        className = ref;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void registerStatusCode(int code) {
        logger.log(Level.FINE, "Class {0} is requesting to change the interface status to {1} from {2}. Allowing change ... ", new Object[]{ className, code, statusCode });
        statusCode = code;
    }

}
package org.apache.catalina.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ControllerException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(ControllerException.class);

    ControllerException(String message) {
        super(message);
        log.error(message);
    }
}

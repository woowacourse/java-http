package org.apache.catalina.exception;

import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ControllerException extends TomcatException {

    private static final Logger log = LoggerFactory.getLogger(ControllerException.class);

    ControllerException(HttpStatusCode httpStatusCode, String message) {
        super(httpStatusCode, message);
        log.error(message);
    }
}

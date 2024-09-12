package org.apache.coyote.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpConnectorException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(HttpConnectorException.class);

    HttpConnectorException(String message) {
        super(message);
        log.error(message);
    }
}

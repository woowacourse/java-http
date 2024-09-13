package org.apache.coyote.exception;

import org.apache.catalina.exception.TomcatException;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpConnectorException extends TomcatException {

    private static final Logger log = LoggerFactory.getLogger(HttpConnectorException.class);

    HttpConnectorException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
        log.error(message);
    }
}
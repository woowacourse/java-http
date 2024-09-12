package org.apache.coyote.http11.exception;

public abstract class HttpConnectorException extends RuntimeException {

     HttpConnectorException(String message) {
        super(message);
    }
}

package org.apache.coyote.http11.exception;

public class RequestException extends RuntimeException {
    public RequestException(final String message) {
        super(message);
    }
}

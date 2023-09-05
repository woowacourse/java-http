package org.apache.coyote.http11.request.exception;

public class HttpMethodNotAllowedException extends RuntimeException {

    public HttpMethodNotAllowedException(final String message) {
        super(message);
    }
}

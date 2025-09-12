package org.apache.coyote.http11.exception;

public class HttpRequestMethodNotSupportedException extends RuntimeException {
    public HttpRequestMethodNotSupportedException(String message) {
        super(message);
    }
}

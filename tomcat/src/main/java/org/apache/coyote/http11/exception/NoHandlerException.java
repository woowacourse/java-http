package org.apache.coyote.http11.exception;

public class NoHandlerException extends RuntimeException {
    public NoHandlerException(String message) {
        super(message);
    }
}

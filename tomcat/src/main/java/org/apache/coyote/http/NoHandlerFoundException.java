package org.apache.coyote.http;

public class NoHandlerFoundException extends RuntimeException {
    public NoHandlerFoundException(String message) {
        super(message);
    }
}

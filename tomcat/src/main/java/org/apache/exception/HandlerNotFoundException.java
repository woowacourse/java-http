package org.apache.exception;

public class HandlerNotFoundException extends RuntimeException {

    public HandlerNotFoundException(String message) {
        super(message);
    }
}

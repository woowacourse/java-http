package org.apache.exception;

public class StaticResourceNotFoundException extends RuntimeException {

    public StaticResourceNotFoundException(String message) {
        super(message);
    }
}

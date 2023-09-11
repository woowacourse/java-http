package org.apache.catalina.exception;

public class CustomBadRequestException extends RuntimeException {

    public CustomBadRequestException() {
    }

    public CustomBadRequestException(final String message) {
        super(message);
    }
}

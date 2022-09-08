package org.apache.coyote.http11.exception;

public class ResourceNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Not Found";

    public ResourceNotFoundException() {
        super(MESSAGE);
    }
}

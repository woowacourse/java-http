package org.apache.coyote.http11.exception;

public class EmptyRequestException extends RuntimeException {

    private static final String MESSAGE = "Empty Request";

    public EmptyRequestException() {
        super(MESSAGE);
    }
}

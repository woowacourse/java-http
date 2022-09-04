package org.apache.coyote.http11;

public class EmptyRequestException extends RuntimeException {

    private static final String MESSAGE = "Empty Request";

    public EmptyRequestException() {
        super(MESSAGE);
    }
}

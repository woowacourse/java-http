package org.apache.coyote.controller.exception;

public class UnauthorizedException extends IllegalArgumentException {

    public UnauthorizedException(final String message) {
        super(message);
    }
}

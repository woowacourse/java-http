package org.apache.coyote.http11.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException() {
    }

    public UnauthorizedException(final String message) {
        super(message);
    }
}

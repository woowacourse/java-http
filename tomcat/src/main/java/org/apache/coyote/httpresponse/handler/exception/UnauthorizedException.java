package org.apache.coyote.httpresponse.handler.exception;

public class UnauthorizedException extends IllegalArgumentException {

    public UnauthorizedException(final String message) {
        super(message);
    }
}

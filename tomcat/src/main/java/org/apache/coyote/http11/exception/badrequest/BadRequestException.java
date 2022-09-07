package org.apache.coyote.http11.exception.badrequest;

public abstract class BadRequestException extends RuntimeException {

    public BadRequestException(final String message) {
        super(message);
    }
}

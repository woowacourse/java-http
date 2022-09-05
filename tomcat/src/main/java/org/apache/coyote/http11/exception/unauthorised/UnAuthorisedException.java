package org.apache.coyote.http11.exception.unauthorised;

public abstract class UnAuthorisedException extends RuntimeException {

    public UnAuthorisedException(final String message) {
        super(message);
    }
}

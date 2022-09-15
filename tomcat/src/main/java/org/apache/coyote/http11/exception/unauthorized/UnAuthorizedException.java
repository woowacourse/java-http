package org.apache.coyote.http11.exception.unauthorized;

public abstract class UnAuthorizedException extends RuntimeException {

    public UnAuthorizedException(final String message) {
        super(message);
    }
}

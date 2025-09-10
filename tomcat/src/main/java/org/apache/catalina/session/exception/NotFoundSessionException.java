package org.apache.catalina.session.exception;

public class NotFoundSessionException extends RuntimeException {

    public NotFoundSessionException() {
    }

    public NotFoundSessionException(final String message) {
        super(message);
    }

    public NotFoundSessionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

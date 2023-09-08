package org.apache.coyote.http11.request.exception;

public class NotFoundHttpVersionException extends IllegalArgumentException {

    public NotFoundHttpVersionException(final String message) {
        super(message);
    }
}

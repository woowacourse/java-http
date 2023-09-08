package org.apache.coyote.http11.request.exception;

public class NotFoundHttpMethodException extends IllegalArgumentException {

    public NotFoundHttpMethodException(final String message) {
        super(message);
    }
}

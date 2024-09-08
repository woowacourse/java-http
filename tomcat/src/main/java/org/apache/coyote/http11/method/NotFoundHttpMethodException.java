package org.apache.coyote.http11.method;

public class NotFoundHttpMethodException extends RuntimeException {
    public NotFoundHttpMethodException(final String message) {
        super(message);
    }
}

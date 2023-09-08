package org.apache.coyote.http11.request.exception;

public class NotFoundQueryStringException extends IllegalArgumentException {

    public NotFoundQueryStringException(final String message) {
        super(message);
    }
}

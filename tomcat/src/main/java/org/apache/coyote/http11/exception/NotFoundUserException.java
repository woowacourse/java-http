package org.apache.coyote.http11.exception;

public class NotFoundUserException extends IllegalArgumentException {

    public NotFoundUserException(final String message) {
        super(message);
    }
}

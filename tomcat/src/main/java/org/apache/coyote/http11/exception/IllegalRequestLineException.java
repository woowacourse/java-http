package org.apache.coyote.http11.exception;

public class IllegalRequestLineException extends IllegalArgumentException {

    public IllegalRequestLineException(final String message) {
        super(message);
    }
}

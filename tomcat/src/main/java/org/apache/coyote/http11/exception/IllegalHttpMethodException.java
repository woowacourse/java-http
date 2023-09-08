package org.apache.coyote.http11.exception;

public class IllegalHttpMethodException extends IllegalArgumentException {

    public IllegalHttpMethodException(final String message) {
        super(message);
    }
}

package org.apache.coyote.http11.controller.exception;

public class UnsupportedHttpMethodException extends IllegalArgumentException {

    public UnsupportedHttpMethodException(final String message) {
        super(message);
    }
}

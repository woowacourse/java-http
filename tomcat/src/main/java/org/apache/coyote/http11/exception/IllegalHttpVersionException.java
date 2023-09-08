package org.apache.coyote.http11.exception;

public class IllegalHttpVersionException extends IllegalRequestLineException {

    public IllegalHttpVersionException(final String message) {
        super(message);
    }
}

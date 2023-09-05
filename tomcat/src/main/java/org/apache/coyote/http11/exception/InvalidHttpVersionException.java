package org.apache.coyote.http11.exception;

public class InvalidHttpVersionException extends RuntimeException{
    public InvalidHttpVersionException(final String message) {
        super(message);
    }
}

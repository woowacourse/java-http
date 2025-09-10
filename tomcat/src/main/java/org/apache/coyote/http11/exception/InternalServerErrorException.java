package org.apache.coyote.http11.exception;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(final String message) {
        super(message);
    }
}

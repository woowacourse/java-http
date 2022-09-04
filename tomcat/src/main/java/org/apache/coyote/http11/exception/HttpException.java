package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatus;

public abstract class HttpException extends RuntimeException {

    private final HttpStatus httpStatus;

    protected HttpException(final String message, final HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

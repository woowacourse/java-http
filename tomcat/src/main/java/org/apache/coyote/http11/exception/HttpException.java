package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatus;

public abstract class HttpException extends RuntimeException{

    private final HttpStatus status;

    public HttpException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public HttpException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

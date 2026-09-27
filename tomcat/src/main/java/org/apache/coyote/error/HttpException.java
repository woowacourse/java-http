package org.apache.coyote.error;

import org.apache.coyote.http11.HttpStatus;

public class HttpException extends RuntimeException {

    private final HttpStatus status;

    public HttpException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}

package org.apache.exception;

import org.apache.coyote.support.HttpStatus;

public class HttpException extends RuntimeException {

    private final HttpStatus status;

    public HttpException(Exception e, HttpStatus status) {
        super(e);
        this.status = status;
    }

    public static HttpException ofNotFound(Exception e) {
        return new HttpException(e, HttpStatus.NOT_FOUND);
    }

    public static HttpException ofInternalServerError(Exception e) {
        return new HttpException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public boolean hasErrorStatus(HttpStatus status) {
        return this.status.equals(status);
    }

    public String toErrorStatus() {
        return status.toResponse();
    }
}

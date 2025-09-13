package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatus;

public class CommonException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CommonException(HttpStatus httpStatus) {
        super(httpStatus.getReasonPhrase());
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

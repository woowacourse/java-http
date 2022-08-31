package org.apache.coyote.web;

import org.apache.coyote.support.HttpStatus;

public class ResponseEntity<T> {

    private final HttpStatus httpStatus;
    private final T response;

    public ResponseEntity(final HttpStatus httpStatus, final T response) {
        this.httpStatus = httpStatus;
        this.response = response;
    }

    public ResponseEntity(final HttpStatus httpStatus) {
        this(httpStatus, null);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public T getResponse() {
        return response;
    }
}

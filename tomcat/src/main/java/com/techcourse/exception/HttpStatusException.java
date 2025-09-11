package com.techcourse.exception;

import com.spring.http.enums.HttpStatus;

public class HttpStatusException extends RuntimeException {

    private final HttpStatus httpStatus;

    public HttpStatusException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpStatusException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }
}

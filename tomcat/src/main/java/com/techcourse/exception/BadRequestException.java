package com.techcourse.exception;

import com.http.enums.HttpStatus;

public class BadRequestException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BadRequestException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

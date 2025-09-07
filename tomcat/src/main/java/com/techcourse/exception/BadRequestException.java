package com.techcourse.exception;

import com.http.enums.HttpStatus;

public class BadRequestException extends HttpStatusException {

    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

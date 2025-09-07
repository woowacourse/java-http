package com.techcourse.exception;

import com.http.enums.HttpStatus;

public class UnAuthorizedException extends HttpStatusException {
    public UnAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

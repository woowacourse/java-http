package com.techcourse.exception;

import com.spring.http.enums.HttpStatus;

public class UnAuthorizedException extends HttpStatusException {
    public UnAuthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

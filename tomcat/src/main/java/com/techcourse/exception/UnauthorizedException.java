package com.techcourse.exception;

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(Exception e) {
        super(e);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

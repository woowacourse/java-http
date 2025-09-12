package com.techcourse.exception;

public class BadRequestException extends UncheckedServletException {

    public BadRequestException(Exception e) {
        super(e);
    }
}

package com.techcourse.app.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException(Exception e) {
        super(e);
    }
}

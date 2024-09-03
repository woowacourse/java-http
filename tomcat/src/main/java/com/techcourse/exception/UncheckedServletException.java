package com.techcourse.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException(Exception e) {
        super(e);
    }

    public UncheckedServletException(String message) {
        super(message);
    }

    public UncheckedServletException(String message, Throwable cause) {
        super(message, cause);
    }
}

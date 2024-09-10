package com.techcourse.exception;

public class UnsupportedMethodException extends RuntimeException {
    public UnsupportedMethodException(Exception e) {
        super(e);
    }

    public UnsupportedMethodException(String message) {
        super(message);
    }
}

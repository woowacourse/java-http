package com.techcourse.app.exception;

public class DuplicatedException extends RuntimeException {
    public DuplicatedException(String message) {
        super(message);
    }
}

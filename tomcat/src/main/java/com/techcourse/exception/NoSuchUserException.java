package com.techcourse.exception;

public class NoSuchUserException extends RuntimeException {

    private final String message;

    public NoSuchUserException(String message) {
        this.message = message;
    }
}

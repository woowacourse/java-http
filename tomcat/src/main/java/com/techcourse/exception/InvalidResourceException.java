package com.techcourse.exception;

public class InvalidResourceException extends RuntimeException {
    public InvalidResourceException(Exception e) {
        super(e);
    }

    public InvalidResourceException(String message) {
        super(message);
    }
}

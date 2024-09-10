package com.techcourse.exception;

public class InvalidRegisterException extends RuntimeException {
    public InvalidRegisterException(Exception e) {
        super(e);
    }

    public InvalidRegisterException(String message) {
        super(message);
    }
}

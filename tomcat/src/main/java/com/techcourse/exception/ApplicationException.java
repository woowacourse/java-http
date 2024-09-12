package com.techcourse.exception;

public abstract class ApplicationException extends RuntimeException {

    ApplicationException(String message) {
        super(message);
    }
}

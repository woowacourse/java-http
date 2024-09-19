package com.techcourse.exception;

public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException() {
        super("password incorrect");
    }
}

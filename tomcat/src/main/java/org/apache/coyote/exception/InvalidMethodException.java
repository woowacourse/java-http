package org.apache.coyote.exception;

public class InvalidMethodException extends RuntimeException {

    private static final String MESSAGE = "유효한 Http Method 가 아닙니다.";

    public InvalidMethodException() {
        super(MESSAGE);
    }
}

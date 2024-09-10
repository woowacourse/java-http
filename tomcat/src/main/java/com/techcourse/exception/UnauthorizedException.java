package com.techcourse.exception;

public class UnauthorizedException extends RuntimeException {

    private static final String MESSAGE = "사용자 인증에 실패하였습니다.";

    public UnauthorizedException() {
        this(MESSAGE);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}

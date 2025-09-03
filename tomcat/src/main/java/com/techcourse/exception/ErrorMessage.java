package com.techcourse.exception;

public enum ErrorMessage {
    ACCOUNT_NOT_FOUND("그런 계정은 없다."),
    INVALID_PASSWORD("비밀번호가 안 맞아."),
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

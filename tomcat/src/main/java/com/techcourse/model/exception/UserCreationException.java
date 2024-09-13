package com.techcourse.model.exception;

public class UserCreationException extends IllegalArgumentException {
    public UserCreationException() {
        super("입력되지 않은 유저 정보가 있습니다.");
    }
}

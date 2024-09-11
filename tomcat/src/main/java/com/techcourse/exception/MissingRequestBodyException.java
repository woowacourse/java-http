package com.techcourse.exception;

public class MissingRequestBodyException extends IllegalArgumentException{
    public MissingRequestBodyException() {
        super("요청 본문이 없습니다.");
    }
}

package com.techcourse.exception;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException() {
        super("이미 존재하는 아이디입니다.");
    }
}

package org.apache.coyote.exception;

public class DuplicateAccountRegisterException extends RuntimeException {

    public DuplicateAccountRegisterException() {
        super("이미 존재하는 사용자입니다.");
    }
}

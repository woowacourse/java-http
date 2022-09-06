package org.apache.coyote.exception;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException() {
        super("계정 정보를 찾지 못했습니다.");
    }
}

package org.apache.catalina.exception;

import nextstep.jwp.exception.NotFoundException;

public class AccountNotFoundException extends NotFoundException {

    public AccountNotFoundException() {
        super("계정 정보를 찾지 못했습니다.");
    }
}

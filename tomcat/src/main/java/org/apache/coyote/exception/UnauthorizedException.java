package org.apache.coyote.exception;

public class UnauthorizedException extends RuntimeException {

    public static final String MESSAGE = "확인되지 않는 사용자 정보입니다.";

    public UnauthorizedException() {
        super(MESSAGE);
    }
}

package org.apache.coyote.http11.exception;

public class UnauthorizedException extends RuntimeException {
    private final String message;

    public UnauthorizedException(final String account) {
        this.message = "인증에 실패하였습니다 :" + account;
    }
}

package org.apache.coyote.http11.component.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("인증 과정 실패");
    }
}

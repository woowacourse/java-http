package org.apache.tomcat.http.exception;

public class AuthenticationException extends RuntimeException {

    public AuthenticationException() {
        super("인증 과정 실패");
    }
}

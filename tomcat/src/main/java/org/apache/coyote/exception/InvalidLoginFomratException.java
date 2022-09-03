package org.apache.coyote.exception;

public class InvalidLoginFomratException extends RuntimeException {

    public InvalidLoginFomratException() {
        super("로그인 요청 형식이 올바르지 않습니다.");
    }
}

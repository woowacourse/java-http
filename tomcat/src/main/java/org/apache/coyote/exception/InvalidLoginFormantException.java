package org.apache.coyote.exception;

public class InvalidLoginFormantException extends RuntimeException {

    public InvalidLoginFormantException() {
        super("로그인 요청 형식이 올바르지 않습니다.");
    }
}

package org.apache.coyote.exception;

public class InvalidLoginFormatException extends RuntimeException {

    public InvalidLoginFormatException() {
        super("로그인 요청 형식이 올바르지 않습니다.");
    }
}

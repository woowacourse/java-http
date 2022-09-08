package org.apache.coyote.exception;

public class InvalidHttpMethodException extends RuntimeException {

    public InvalidHttpMethodException() {
        super("올바르지 않은 HTTP 메서드입니다.");
    }
}

package org.apache.coyote.exception;

public class InvalidHttpMethodException extends RuntimeException {

    public InvalidHttpMethodException() {
        super("잘못된 HTTP Method 입니다.");
    }
}

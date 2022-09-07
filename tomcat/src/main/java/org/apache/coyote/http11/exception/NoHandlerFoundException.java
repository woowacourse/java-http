package org.apache.coyote.http11.exception;

public class NoHandlerFoundException extends RuntimeException {

    public NoHandlerFoundException() {
        super("요청을 처리할 수 없습니다.");
    }
}

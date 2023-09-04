package org.apache.coyote.http11.exception;

public class InvalidRequestPathException extends IllegalArgumentException {

    public InvalidRequestPathException() {
        super("처리할 수 없는 요청입니다.");
    }
}

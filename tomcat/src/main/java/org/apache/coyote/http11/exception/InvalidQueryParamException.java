package org.apache.coyote.http11.exception;

public class InvalidQueryParamException extends RuntimeException {

    public InvalidQueryParamException() {
        super("잘못된 요청입니다.");
    }
}

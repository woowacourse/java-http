package org.apache.coyote.http11.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException() {
        super("잘못된 요청입니다.");
    }
}

package org.apache.coyote.http11.exception;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException() {
        super("올바르지 않은 요청입니다.");
    }
}

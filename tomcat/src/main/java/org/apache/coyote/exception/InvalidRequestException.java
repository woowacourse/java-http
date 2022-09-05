package org.apache.coyote.exception;

public class InvalidRequestException extends RuntimeException {

    public static final String INVALID_REQUEST_MESSAGE = "올바르지 않은 Http 요청 메세지 입니다.";

    public InvalidRequestException() {
        super(INVALID_REQUEST_MESSAGE);
    }
}

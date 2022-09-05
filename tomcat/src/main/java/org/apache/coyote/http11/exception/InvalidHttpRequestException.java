package org.apache.coyote.http11.exception;

public class InvalidHttpRequestException extends RuntimeException {

    private static final String MESSAGE = "잘못된 HTTP 요청입니다.";

    public InvalidHttpRequestException() {
        super(MESSAGE);
    }
}

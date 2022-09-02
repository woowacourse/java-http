package org.apache.coyote.http11.exception;

public class InvalidHttpRequestStartLineException extends RuntimeException {

    private static final String MESSAGE = "잘못된 HTTP 요청입니다.";

    public InvalidHttpRequestStartLineException() {
        super(MESSAGE);
    }
}

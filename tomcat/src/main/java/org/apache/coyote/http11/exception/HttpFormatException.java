package org.apache.coyote.http11.exception;

public class HttpFormatException extends RuntimeException {

    private static final String MESSAGE = "올바르지 않은 HTTP 형식입니다.";

    public HttpFormatException() {
        this(MESSAGE);
    }

    public HttpFormatException(String message) {
        super(message);
    }
}

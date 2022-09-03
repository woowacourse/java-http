package org.apache.coyote.exception;

public class InvalidHttpRequestFormatException extends RuntimeException {

    public InvalidHttpRequestFormatException() {
        super("HTTP Request 형식이 올바르지 않습니다.");
    }
}

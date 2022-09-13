package org.apache.coyote.request.exception;

public class InvalidRequestLineFormatException extends RuntimeException {

    public InvalidRequestLineFormatException() {
        super("Request Line 형식이 일치하지 않습니다.");
    }
}

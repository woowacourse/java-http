package org.apache.coyote.http11.exception;

public class InvalidHttp11RequestLineException extends RuntimeException {

    public InvalidHttp11RequestLineException() {
        super("유효하지 않은 Request line 입니다.");
    }
}

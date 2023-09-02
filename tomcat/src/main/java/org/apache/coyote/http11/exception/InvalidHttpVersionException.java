package org.apache.coyote.http11.exception;

public class InvalidHttpVersionException extends RuntimeException {

    public InvalidHttpVersionException() {
        super("HTTP 버전이 올바르지 않습니다.");
    }
}

package org.apache.coyote.http11.exception;

public class UnsupportedHttpMethodException extends RuntimeException {

    public UnsupportedHttpMethodException() {
        super("지원하지 않는 Http Method입니다.");
    }
}

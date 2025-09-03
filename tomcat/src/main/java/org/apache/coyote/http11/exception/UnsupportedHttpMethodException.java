package org.apache.coyote.http11.exception;

public class UnsupportedHttpMethodException extends RuntimeException {
    public UnsupportedHttpMethodException(String name) {
        super("지원하지 않는 Http 메서드: " + name);
    }
}

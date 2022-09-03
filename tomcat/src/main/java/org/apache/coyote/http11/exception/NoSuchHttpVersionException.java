package org.apache.coyote.http11.exception;

public class NoSuchHttpVersionException extends RuntimeException {

    public NoSuchHttpVersionException(final String message) {
        super(message);
    }

    public NoSuchHttpVersionException() {
        this("존재하지 않는 http version 입니다.");
    }
}

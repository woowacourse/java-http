package org.apache.coyote.http11.exception;

public class NoSuchHeaderException extends RuntimeException {

    public NoSuchHeaderException(final String message) {
        super(message);
    }

    public NoSuchHeaderException() {
        this("존재하지 않는 header 입니다.");
    }
}

package org.apache.coyote.http11.exception;

public class NoSuchContentTypeException extends RuntimeException {

    public NoSuchContentTypeException(final String message) {
        super(message);
    }

    public NoSuchContentTypeException() {
        this("존재하지 않는 ContentType 입니다.");
    }
}

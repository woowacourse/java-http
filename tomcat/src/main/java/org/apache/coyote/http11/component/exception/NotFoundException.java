package org.apache.coyote.http11.component.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("자원을 찾을 수 없음");
    }

    public NotFoundException(final String message) {
        super(message);
    }
}

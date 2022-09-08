package org.apache.coyote.http11.exception;

public class MethodNotAllowedException extends RuntimeException {

    private static final String MESSAGE = "지원하지 않는 HTTP 메서드입니다.";

    public MethodNotAllowedException() {
        super(MESSAGE);
    }
}

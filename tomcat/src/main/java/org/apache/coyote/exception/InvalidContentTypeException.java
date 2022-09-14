package org.apache.coyote.exception;

public class InvalidContentTypeException extends RuntimeException {

    private static final String MESSAGE = "유효한 Content Type 이 아닙니다.";

    public InvalidContentTypeException() {
        super(MESSAGE);
    }
}

package org.apache.coyote.http11.exception;

public class FaviconNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Favicon이 존재하지 않습니다.";

    public FaviconNotFoundException() {
        super(MESSAGE);
    }
}

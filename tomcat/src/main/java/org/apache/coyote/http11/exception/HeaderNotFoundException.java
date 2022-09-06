package org.apache.coyote.http11.exception;

public class HeaderNotFoundException extends RuntimeException{

    private static final String MESSAGE = "헤더가 존재하지 않습니다";

    public HeaderNotFoundException() {
        super(MESSAGE);
    }
}

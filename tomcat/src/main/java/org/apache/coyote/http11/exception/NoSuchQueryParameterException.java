package org.apache.coyote.http11.exception;

public class NoSuchQueryParameterException extends RuntimeException {

    public NoSuchQueryParameterException(final String message) {
        super(message);
    }

    public NoSuchQueryParameterException() {
        this("존재하지 않는 query parameter 입니다.");
    }
}

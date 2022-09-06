package org.apache.coyote.http11.exception;

public class RequestHeaderNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Request Header Not Found";

    public RequestHeaderNotFoundException() {
        super(MESSAGE);
    }
}

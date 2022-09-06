package org.apache.coyote.http11.exception;

public class HttpMethodNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Http Method Not Found";

    public HttpMethodNotFoundException() {
        super(MESSAGE);
    }
}

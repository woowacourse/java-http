package org.apache.catalina.exception;

public class HttpVersionNotSupported extends RuntimeException {
    public HttpVersionNotSupported(String message) {
        super(message);
    }
}

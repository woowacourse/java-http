package org.apache.coyote.http11;

public class InvalidHttpRequestException extends RuntimeException {

    public InvalidHttpRequestException(String message) {
        super(message);
    }

    public InvalidHttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

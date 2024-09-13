package org.apache.coyote.http11.exception;

public class CantHandleRequestException extends RuntimeException {
    public CantHandleRequestException(String message) {
        super(message);
    }
}

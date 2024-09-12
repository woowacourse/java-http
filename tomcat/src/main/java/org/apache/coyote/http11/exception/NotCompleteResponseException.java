package org.apache.coyote.http11.exception;

public class NotCompleteResponseException extends RuntimeException {
    public NotCompleteResponseException(String message) {
        super(message);
    }
}

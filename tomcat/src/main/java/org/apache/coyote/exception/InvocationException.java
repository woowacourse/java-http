package org.apache.coyote.exception;

public class InvocationException extends RuntimeException {
    public InvocationException(Throwable cause, String message) {
        super(message, cause);
    }
}

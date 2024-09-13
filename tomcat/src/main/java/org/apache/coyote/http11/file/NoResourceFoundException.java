package org.apache.coyote.http11.file;

public class NoResourceFoundException extends RuntimeException {

    public NoResourceFoundException(String message) {
        super(message);
    }
}

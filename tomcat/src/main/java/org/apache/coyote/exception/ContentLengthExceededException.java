package org.apache.coyote.exception;

public class ContentLengthExceededException extends RuntimeException {

    public ContentLengthExceededException(final String message) {
        super(message);
    }
}

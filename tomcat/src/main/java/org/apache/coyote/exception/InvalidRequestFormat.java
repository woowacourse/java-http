package org.apache.coyote.exception;

public class InvalidRequestFormat extends UncheckedServletException {
    public InvalidRequestFormat(final String message) {
        super(message);
    }
}


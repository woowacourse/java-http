package org.apache.coyote.http11.exception.notfound;

public abstract class NotFoundException extends RuntimeException {

    public NotFoundException(final String message) {
        super(message);
    }
}

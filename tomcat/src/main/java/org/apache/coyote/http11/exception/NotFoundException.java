package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatus;

public final class NotFoundException extends HttpException {

    public NotFoundException(final String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

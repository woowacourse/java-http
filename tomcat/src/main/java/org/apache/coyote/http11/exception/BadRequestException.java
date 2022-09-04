package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatus;

public final class BadRequestException extends HttpException {

    public BadRequestException(final String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

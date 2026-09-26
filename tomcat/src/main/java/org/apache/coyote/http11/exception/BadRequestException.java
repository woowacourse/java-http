package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.HttpStatus;

public class BadRequestException extends HttpException {
    public BadRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public BadRequestException(final String message, final Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}

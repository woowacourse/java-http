package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatus;

public class NotImplementedException extends HttpException {
    public NotImplementedException(final String message) {
        super(HttpStatus.NOT_IMPLEMENTED, message);
    }

    public NotImplementedException(final String message, final Throwable cause) {
        super(HttpStatus.NOT_IMPLEMENTED, message, cause);
    }
}

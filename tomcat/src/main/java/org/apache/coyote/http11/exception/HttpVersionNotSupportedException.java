package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.HttpStatus;

public class HttpVersionNotSupportedException extends HttpException{
    public HttpVersionNotSupportedException(final String message) {
        super(HttpStatus.NOT_IMPLEMENTED, message);
    }

    public HttpVersionNotSupportedException(final String message, final Throwable cause) {
        super(HttpStatus.NOT_IMPLEMENTED, message, cause);
    }
}

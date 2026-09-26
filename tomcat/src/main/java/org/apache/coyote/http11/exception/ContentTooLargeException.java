package org.apache.coyote.http11.exception;

import org.apache.coyote.http11.response.HttpStatus;

public class ContentTooLargeException extends HttpException{
    public ContentTooLargeException(final String message) {
        super(HttpStatus.CONTENT_TOO_LARGE, message);
    }

    public ContentTooLargeException(final String message, final Throwable cause) {
        super(HttpStatus.CONTENT_TOO_LARGE, message, cause);
    }
}

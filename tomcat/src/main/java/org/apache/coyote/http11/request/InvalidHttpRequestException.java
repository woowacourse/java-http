package org.apache.coyote.http11.request;

public final class InvalidHttpRequestException extends RuntimeException {

    InvalidHttpRequestException(final String message) {
        super(message);
    }

    InvalidHttpRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

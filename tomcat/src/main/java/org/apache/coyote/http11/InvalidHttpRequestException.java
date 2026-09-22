package org.apache.coyote.http11;

final class InvalidHttpRequestException extends RuntimeException {

    InvalidHttpRequestException(final String message) {
        super(message);
    }

    InvalidHttpRequestException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

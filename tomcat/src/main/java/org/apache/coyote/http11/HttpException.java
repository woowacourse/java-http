package org.apache.coyote.http11;

class HttpException extends RuntimeException {

    private final HttpStatus status;

    HttpException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    HttpStatus status() {
        return status;
    }
}

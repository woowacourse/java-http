package org.apache.coyote.http11;

public class HttpException extends RuntimeException {

    private final HttpStatus status;

    public HttpException(final HttpStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus status() {
        return status;
    }
}

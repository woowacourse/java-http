package org.apache.coyote.http11;

public class HttpException extends RuntimeException {

    private final HttpStatus httpStatus;

    public HttpException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}

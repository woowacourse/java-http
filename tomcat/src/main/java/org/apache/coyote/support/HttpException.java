package org.apache.coyote.support;

public class HttpException extends RuntimeException {

    private final HttpStatus status;

    public HttpException(HttpStatus status) {
        this.status = status;
    }

    public HttpException(Exception e, HttpStatus status) {
        super(e);
        this.status = status;
    }

    public static HttpException ofNotFound(Exception e) {
        return new HttpException(e, HttpStatus.NOT_FOUND);
    }

    public static HttpException ofInternalServerError(Exception e) {
        return new HttpException(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public HttpStatus getStatus() {
        return status;
    }
}

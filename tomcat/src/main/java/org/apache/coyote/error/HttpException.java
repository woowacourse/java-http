package org.apache.coyote.error;

import org.apache.coyote.http11.HttpStatus;

public class HttpException extends RuntimeException {

    private final HttpStatus status;

    public HttpException(final HttpStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public static HttpException methodNotAllowed() {
        return new HttpException(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다.");
    }

    public HttpStatus status() {
        return status;
    }
}

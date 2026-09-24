package org.apache.coyote.http11.response;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401);

    private final int statusCode;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    int statusCode() {
        return statusCode;
    }
}

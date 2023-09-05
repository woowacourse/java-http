package org.apache.coyote.http11.common;

public enum HttpStatus {
    OK(200),
    MOVED_PERMANENTLY(301),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500),
    ;

    private final int statusCode;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

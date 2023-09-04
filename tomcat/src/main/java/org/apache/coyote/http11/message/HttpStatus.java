package org.apache.coyote.http11.message;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    NOT_FOUND(404);

    private final int statusCode;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

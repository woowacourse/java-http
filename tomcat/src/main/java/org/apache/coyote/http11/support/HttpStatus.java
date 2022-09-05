package org.apache.coyote.http11.support;

public enum HttpStatus {

    OK(200),
    FOUND(302),
    UNAUTHORIZED(401),
    INTERNAL_SERVER_ERROR(500);

    private final int statusCode;

    HttpStatus(final int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return String.valueOf(statusCode);
    }
}

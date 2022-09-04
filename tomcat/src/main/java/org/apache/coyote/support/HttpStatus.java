package org.apache.coyote.support;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    UNAUTHORIZED(401, "UNAUTHORIZED"), NOT_FOUND(404, "NOTFOUND");

    private final int statusCode;
    private final String message;

    HttpStatus(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}

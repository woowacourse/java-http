package org.apache.coyote.support;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "FOUND"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    private final int statusCode;
    private final String message;

    HttpStatus(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public boolean isErrorCode() {
        return this.statusCode / 100 == 4 || this.statusCode / 100 == 5;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}

package org.apache.coyote.http11.support;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String statusMessage;

    HttpStatus(final int statusCode, final String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusCode() {
        return String.valueOf(statusCode);
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}

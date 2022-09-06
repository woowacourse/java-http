package org.apache.coyote.http11.support;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final int statusCode;
    private final String value;

    HttpStatus(final int statusCode, final String value) {
        this.statusCode = statusCode;
        this.value = value;
    }

    public String getStatusCode() {
        return String.valueOf(statusCode);
    }

    public String getValue() {
        return value;
    }
}

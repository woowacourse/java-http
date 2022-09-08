package org.apache.coyote.response;

public enum StatusCode {
    OK("200 OK"),
    FOUND("302 Found"),
    BAD_REQUEST("400 Bad Request"),
    UNAUTHORIZED("401 Unauthorized"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    private final String statusCode;

    StatusCode(final String statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return this.statusCode;
    }
}

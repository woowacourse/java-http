package org.apache.coyote.http11.util;

public enum HttpStatus {

    OK("200 OK", "200"),
    FOUND("302 Found", "302"),
    BAD_REQUEST("400 Bad Request", "400"),
    UNAUTHORIZED("401 Unauthorized", "401"),
    NOT_FOUND("404 Not Found", "404");

    private final String httpStatus;
    private final String value;

    HttpStatus(final String httpStatus, final String value) {
        this.httpStatus = httpStatus;
        this.value = value;
    }

    public String getHttpStatus() {
        return httpStatus;
    }

    public String getValue() {
        return value;
    }
}

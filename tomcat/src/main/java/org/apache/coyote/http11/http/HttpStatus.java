package org.apache.coyote.http11.http;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found"),
    UNAUTHORIZED("401 Unauthorized");

    private final String value;

    HttpStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

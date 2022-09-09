package org.apache.coyote.http11.util;

public enum HttpStatus {
    OK("200 Ok"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found");

    private final String value;

    HttpStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package org.apache.coyote.http11.util;

public enum HttpStatus {
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found"),
    OK("200 Ok");

    private final String value;

    HttpStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

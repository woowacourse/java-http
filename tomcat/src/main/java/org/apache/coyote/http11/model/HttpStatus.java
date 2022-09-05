package org.apache.coyote.http11.model;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found");

    private final String value;

    HttpStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

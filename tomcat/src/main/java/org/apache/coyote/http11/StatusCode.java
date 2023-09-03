package org.apache.coyote.http11;

public enum StatusCode {
    OK("200 OK"),
    FOUND("302 FOUND"),
    UNAUTHORIZED("401 UNAUTHORIZED"),
    NOT_FOUND("404 NOT FOUND");

    private String value;

    StatusCode(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

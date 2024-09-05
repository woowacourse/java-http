package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 FOUND"),
    UNAUTHORIZED("401 UNAUTHORIZED"),
    NOT_FOUND("404 NOT FOUND"),
    INTERNAL_SERVER_ERROR("500 INTERNAL SERVER ERROR");

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

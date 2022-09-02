package org.apache.coyote.http11.response.element;

public enum HttpStatus {
    OK("200 OK"),
    NOT_FOUND("404 NOT FOUND"),
    UNAUTHORIZED("401 UNAUTHORIZED");

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

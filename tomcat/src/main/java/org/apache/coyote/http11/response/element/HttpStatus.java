package org.apache.coyote.http11.response.element;

public enum HttpStatus {
    OK("200 OK"),
    NOT_FOUND("404 Not Found"),
    UNAUTHORIZED("401 Unauthorized"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error"),
    FOUND("302 Found"),
    ;

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK("200 OK"),
    FOUND("302 Found"),
    UNAUTHORIZED("401 Unauthorized"),
    NOT_FOUND("404 Not Found"),
    INTERNAL_SERVER_ERROR("500 Internal Server Error");

    private final String value;

    HttpStatusCode(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

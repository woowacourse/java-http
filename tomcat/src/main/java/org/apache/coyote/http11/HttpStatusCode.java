package org.apache.coyote.http11;

public enum HttpStatusCode {

    OK("200 OK"),
    NOT_FOUND("404 NOT FOUND");

    private final String value;

    HttpStatusCode(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

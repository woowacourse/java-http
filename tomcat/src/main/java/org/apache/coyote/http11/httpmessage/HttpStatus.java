package org.apache.coyote.http11.httpmessage;

public enum HttpStatus {

    OK("200 OK"),
    FOUND("302 FOUND"),
    UNAUTHORIZED("401 UNAUTHORIZED");

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

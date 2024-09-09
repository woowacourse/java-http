package org.apache.coyote.http11.startline;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    UNAUTHORIZED("401 Unauthorized"),
    ;

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package org.apache.coyote.http11;

public enum StatusCode {
    OK("200 OK"),
    FOUND("302 FOUND"),
    ;

    private final String value;

    StatusCode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

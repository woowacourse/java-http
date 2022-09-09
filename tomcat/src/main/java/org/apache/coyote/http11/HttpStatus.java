package org.apache.coyote.http11;

public enum HttpStatus {

    OK("200 OK"),
    FOUND("302 Found");

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package org.apache.coyote.http11;

public enum HttpStatus {
    OK("200 OK");

    private final String value;

    HttpStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

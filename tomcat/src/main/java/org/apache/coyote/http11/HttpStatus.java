package org.apache.coyote.http11;

public enum HttpStatus {

    OK("HTTP/1.1 200 OK "),
    FOUND("HTTP/1.1 302 Found "),
    ;

    private final String value;

    HttpStatus(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package org.apache.coyote.http11.common;

public enum HttpMessageDelimiter {

    WORD(" "),
    LINE("\r\n"),
    HEADER_BODY(""),
    ;

    private final String value;

    HttpMessageDelimiter(final String value) {
        this.value = value;
    }

    public boolean isDifference(final String other) {
        return !value.equals(other);
    }

    public String getValue() {
        return value;
    }
}

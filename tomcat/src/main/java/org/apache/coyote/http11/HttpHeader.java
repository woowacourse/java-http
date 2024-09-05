package org.apache.coyote.http11;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

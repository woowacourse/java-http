package org.apache.coyote.http11.common;

public enum HttpHeaderType {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String value;

    HttpHeaderType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

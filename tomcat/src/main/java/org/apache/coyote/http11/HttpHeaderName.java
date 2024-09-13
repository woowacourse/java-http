package org.apache.coyote.http11;

public enum HttpHeaderName {

    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type");

    private final String value;

    HttpHeaderName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

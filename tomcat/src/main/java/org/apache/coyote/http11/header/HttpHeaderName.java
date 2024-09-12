package org.apache.coyote.http11.header;

public enum HttpHeaderName {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String value;

    HttpHeaderName(String name) {
        this.value = name;
    }

    public String getName() {
        return value;
    }
}

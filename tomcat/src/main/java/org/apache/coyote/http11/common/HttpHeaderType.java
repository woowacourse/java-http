package org.apache.coyote.http11.common;

public enum HttpHeaderType {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ACCEPT("Accept");

    private final String name;

    HttpHeaderType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

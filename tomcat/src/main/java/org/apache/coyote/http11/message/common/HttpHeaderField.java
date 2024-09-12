package org.apache.coyote.http11.message.common;

public enum HttpHeaderField {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");

    private final String name;

    HttpHeaderField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

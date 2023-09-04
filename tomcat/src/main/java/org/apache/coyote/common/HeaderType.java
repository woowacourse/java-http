package org.apache.coyote.common;

public enum HeaderType {

    ACCEPT("Accept"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    CONNECTION("Connection"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String source;

    HeaderType(final String source) {
        this.source = source;
    }

    public String source() {
        return source;
    }
}

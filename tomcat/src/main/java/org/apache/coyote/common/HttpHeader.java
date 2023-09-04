package org.apache.coyote.common;

public enum HttpHeader {

    ACCEPT("Accept"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    CONNECTION("Connection"),
    LOCATION("Location"),
    COOKIE("Cookies"),
    SET_COOKIE("Set-Cookie");

    private final String source;

    HttpHeader(final String source) {
        this.source = source;
    }

    public String source() {
        return source;
    }
}

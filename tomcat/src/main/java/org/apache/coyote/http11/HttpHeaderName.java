package org.apache.coyote.http11;

public enum HttpHeaderName {

    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie")
    ;

    private final String name;

    HttpHeaderName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

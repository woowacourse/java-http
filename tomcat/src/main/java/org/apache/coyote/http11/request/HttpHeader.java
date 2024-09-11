package org.apache.coyote.http11.request;

public enum HttpHeader {
    LOCATION("Location"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie")
    ;

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

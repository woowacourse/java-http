package org.apache.coyote.http11.header;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

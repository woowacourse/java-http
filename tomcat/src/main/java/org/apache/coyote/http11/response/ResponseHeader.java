package org.apache.coyote.http11.response;

public enum ResponseHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");

    private final String name;

    ResponseHeader(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

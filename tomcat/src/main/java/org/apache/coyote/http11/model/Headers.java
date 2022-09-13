package org.apache.coyote.http11.model;

public enum Headers {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    LOCATION("Location");

    private final String name;

    Headers(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

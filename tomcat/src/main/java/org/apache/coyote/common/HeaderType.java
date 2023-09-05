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

    private final String value;

    HeaderType(final String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

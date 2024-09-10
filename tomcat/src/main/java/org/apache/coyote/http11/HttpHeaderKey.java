package org.apache.coyote.http11;

public enum HttpHeaderKey {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    CONNECTION("Connection"),
    SET_COOKIE("Set-Cookie"),
    HOST("Host");

    private final String name;

    HttpHeaderKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

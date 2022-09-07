package org.apache.coyote.http11.model;

public enum Header {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String key;

    Header(final String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

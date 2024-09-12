package org.apache.coyote.http11.protocol.enums;

public enum HeaderKey {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String key;

    HeaderKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}

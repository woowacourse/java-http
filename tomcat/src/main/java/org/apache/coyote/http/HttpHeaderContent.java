package org.apache.coyote.http;

public enum HttpHeaderContent {

    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type");

    private final String value;

    HttpHeaderContent(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

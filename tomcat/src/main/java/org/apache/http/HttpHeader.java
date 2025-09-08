package org.apache.http;

public enum HttpHeader {
    LOCATION("Location"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

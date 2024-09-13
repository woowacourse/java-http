package org.apache.coyote.http;

public enum HttpHeader {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie"),
    ;

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

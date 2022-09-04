package org.apache.coyote.support;

public enum HttpHeader {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    ACCEPT_ENCODING("Accept-Encoding"),
    ACCEPT_LANGUAGE("Accept-Language"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

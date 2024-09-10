package org.apache.catalina.http.header;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    ;

    private final String value;

    HttpHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

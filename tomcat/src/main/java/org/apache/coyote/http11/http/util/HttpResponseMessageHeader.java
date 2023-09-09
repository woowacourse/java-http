package org.apache.coyote.http11.http.util;

public enum HttpResponseMessageHeader {

    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    NONE(""),
    ;

    private final String value;

    HttpResponseMessageHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

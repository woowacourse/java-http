package org.apache.coyote.http11.header;

public enum ResponseHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String value;

    ResponseHeader(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

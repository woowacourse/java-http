package org.apache.coyote.http11;

public enum HttpHeaderField {

    LOCATION("Location"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String value;

    HttpHeaderField(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

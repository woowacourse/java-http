package org.apache.coyote.http;

public enum HttpHeaders {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String value;

    HttpHeaders(final String value) {
        this.value = value;
    }

    public boolean isSameValue(final String value) {
        return this.value.equals(value);
    }

    public String getValue() {
        return value;
    }
}

package org.apache.coyote.http11.message;

public enum HttpHeaders {

    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location");

    private final String value;

    HttpHeaders(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}

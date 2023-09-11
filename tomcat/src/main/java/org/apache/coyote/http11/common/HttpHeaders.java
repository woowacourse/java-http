package org.apache.coyote.http11.common;

public enum HttpHeaders {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");

    private final String header;

    HttpHeaders(final String header) {
        this.header = header;
    }

    public String getMessage() {
        return header;
    }
}

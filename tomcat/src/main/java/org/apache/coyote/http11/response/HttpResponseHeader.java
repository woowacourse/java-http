package org.apache.coyote.http11.response;

public enum HttpResponseHeader {

    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    private final String header;

    HttpResponseHeader(final String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }
}

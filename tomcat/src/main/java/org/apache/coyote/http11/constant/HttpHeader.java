package org.apache.coyote.http11.constant;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location");

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String value() {
        return headerName;
    }
}

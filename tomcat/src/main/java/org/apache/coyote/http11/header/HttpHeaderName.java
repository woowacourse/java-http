package org.apache.coyote.http11.header;

public enum HttpHeaderName {

    LOCATION("Location"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie")
    ;

    private final String headerName;

    HttpHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}

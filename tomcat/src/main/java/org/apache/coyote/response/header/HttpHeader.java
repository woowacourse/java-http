package org.apache.coyote.response.header;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    ;

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String headerName() {
        return headerName;
    }
}

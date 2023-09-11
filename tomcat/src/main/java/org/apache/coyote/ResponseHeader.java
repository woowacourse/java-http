package org.apache.coyote;

public enum ResponseHeader {
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    ;

    private final String headerName;

    ResponseHeader(final String headerName) {
        this.headerName = headerName;
    }

    public String getName() {
        return headerName;
    }
}

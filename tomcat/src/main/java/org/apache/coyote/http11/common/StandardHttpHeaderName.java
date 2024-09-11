package org.apache.coyote.http11.common;

public enum StandardHttpHeaderName {

    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    COOKIE("Cookie");

    private final String headerName;

    StandardHttpHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getName() {
        return headerName;
    }
}

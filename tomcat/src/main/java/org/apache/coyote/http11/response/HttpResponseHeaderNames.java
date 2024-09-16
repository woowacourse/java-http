package org.apache.coyote.http11.response;

public enum HttpResponseHeaderNames {

    COOKIE("Cookie"),
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location");

    private final String headerName;

    HttpResponseHeaderNames(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}

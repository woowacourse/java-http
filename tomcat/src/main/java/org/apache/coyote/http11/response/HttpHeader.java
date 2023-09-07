package org.apache.coyote.http11.response;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),

    ;

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String headerName() {
        return headerName;
    }
}

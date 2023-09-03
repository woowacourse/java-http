package org.apache.coyote.httpresponse.header;

public enum ResponseHeaderType {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    private final String headerName;

    ResponseHeaderType(final String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }
}

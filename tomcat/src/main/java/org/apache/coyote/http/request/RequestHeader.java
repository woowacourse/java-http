package org.apache.coyote.http.request;

import org.apache.coyote.http.Header;

public class RequestHeader {

    private final Header header;

    public RequestHeader(Header header) {
        this.header = header;
    }

    public boolean hasHeader(String headerName) {
        return header.hasHeader(headerName);
    }

    public String getValue(String headerName) {
        return header.getKey(headerName);
    }

    public String toResponse() {
        return header.toResponse();
    }
}

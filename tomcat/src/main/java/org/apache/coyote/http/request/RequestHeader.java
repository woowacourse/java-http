package org.apache.coyote.http.request;

import org.apache.coyote.http.Header;
import org.apache.coyote.http.HttpCookie;

public class RequestHeader {

    private final Header header;

    public RequestHeader(Header header) {
        this.header = header;
    }

    public boolean hasHeader(String headerName) {
        return header.hasHeader(headerName);
    }

    public HttpCookie getCookie() {
        if (header.hasHeader("Cookie")) {
            return HttpCookie.of(header.getValue("Cookie"));
        }
        return new HttpCookie();
    }

    public String getValue(String headerName) {
        return header.getValue(headerName);
    }

    public String toResponse() {
        return header.toResponse();
    }
}

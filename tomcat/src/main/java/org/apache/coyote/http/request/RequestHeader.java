package org.apache.coyote.http.request;

import org.apache.coyote.http.Header;
import org.apache.coyote.http.HttpCookie;

public class RequestHeader {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final Header header;

    public RequestHeader(Header header) {
        this.header = header;
    }

    public boolean hasHeader(String headerName) {
        return header.hasHeader(headerName);
    }

    public HttpCookie getCookie() {
        if (header.hasHeader(COOKIE)) {
            return HttpCookie.of(header.getValue(COOKIE));
        }
        return new HttpCookie();
    }

    public String getContentLength() {
        return header.getValue(CONTENT_LENGTH);
    }

    public String getValue(String headerName) {
        return header.getValue(headerName);
    }

    public String toResponse() {
        return header.toResponse();
    }
}

package org.apache.coyote.http11.request;

import java.util.Map;

public class HttpRequestHeader {
    private final Map<String, String> headers;

    public HttpRequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(this.headers.get("Cookie"));
    }

    public String getAccept() {
        return this.headers.get("Accept");
    }

    public String getContentLength() {
        return this.headers.get("Content-Length");
    }

}

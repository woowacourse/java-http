package org.apache.coyote.support;

import java.util.Map;

public class HttpHeaders {

    private final Map<HttpHeader, String> headers;

    public HttpHeaders(final Map<HttpHeader, String> headers) {
        this.headers = headers;
    }

    public Map<HttpHeader, String> getHeaders() {
        return headers;
    }

    public void add(final HttpHeader key, final String value) {
        headers.put(key, value);
    }
}

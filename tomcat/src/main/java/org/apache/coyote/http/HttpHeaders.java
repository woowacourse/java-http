package org.apache.coyote.http;

import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public String get(final String headerName) {
        return headers.get(headerName);
    }

    public boolean containsKey(final String headerName) {
        return headers.containsKey(headerName);
    }
}

package org.apache.coyote.support;

import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void add(final String key, final String value) {
        headers.put(key, value);
    }
}

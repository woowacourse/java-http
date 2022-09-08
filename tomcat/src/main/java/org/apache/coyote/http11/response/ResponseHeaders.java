package org.apache.coyote.http11.response;

import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(final String key, final String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

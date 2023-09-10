package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    private ResponseHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    protected ResponseHeaders() {
        this.headers = new HashMap<>();
    }

    protected void addHeader(String key, String value) {
        headers.put(key, value);
    }
}

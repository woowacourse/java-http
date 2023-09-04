package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private static final String DEFAULT_VALUE = "";

    private final Map<String, String> headers;

    public HttpHeaders() {
        headers = new HashMap<>();
    }

    public void add(final String key, final String value) {
        headers.put(key, value);
    }

    public String get(final String headerName) {
        return headers.getOrDefault(headerName, DEFAULT_VALUE);
    }

    public String getContentType() {
        String value = headers.getOrDefault("Content-Type", DEFAULT_VALUE);
        if (value.isEmpty()) {
            value = headers.get("Accept");
        }
        return value.split(",")[0];
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

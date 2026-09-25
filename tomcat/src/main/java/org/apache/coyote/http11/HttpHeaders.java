package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders() {
        headers = new HashMap<>();
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

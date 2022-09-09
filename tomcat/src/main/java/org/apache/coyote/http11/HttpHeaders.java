package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private Map<String, String> headers = new HashMap<>();

    public HttpHeaders() {
    }

    public void add(String key, String value) {
        headers.put(key, value);
    }

    public String getOrDefault(String key, String defaultValue) {
        return headers.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

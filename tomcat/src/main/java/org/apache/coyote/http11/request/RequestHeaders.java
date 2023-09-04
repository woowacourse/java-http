package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders() {
        this.headers = new HashMap<>();
    }

    public void set(final String key, final String value) {
        headers.put(key, value);
    }

    public String get(final String key) {
        return headers.get(key);
    }

    public boolean contains(final String key) {
        return headers.containsKey(key);
    }
}

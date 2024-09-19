package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeaders {

    private final Map<String, String> headers;

    public HttpResponseHeaders() {
        this.headers = new HashMap<>();
    }

    public void setAttribute(String key, String value) {
        this.headers.put(key, value);
    }

    public String[] getKeys() {
        return headers.keySet().toArray(new String[0]);
    }

    public String getValue(String key) {
        return headers.get(key);
    }
}

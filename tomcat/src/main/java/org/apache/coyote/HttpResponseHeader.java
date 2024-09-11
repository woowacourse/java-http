package org.apache.coyote;

import java.util.HashMap;
import java.util.Map;

public class HttpResponseHeader {

    private final Map<String, String> headers;

    public HttpResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

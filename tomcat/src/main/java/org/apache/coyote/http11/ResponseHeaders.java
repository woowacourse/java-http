package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, String> headers;

    public ResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static ResponseHeaders empty() {
        return new ResponseHeaders(new HashMap<>());
    }

    public void put(final String key, final String value) {
        headers.put(key, value);
    }

    public String get(final String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

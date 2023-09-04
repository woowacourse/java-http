package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders() {
        headers = new HashMap<>();
    }

    public void add(final String key, final String value) {
        headers.put(key, value);
    }

    public String getContentType() {
        final String value = headers.get("Accept");
        return value.split(",")[0];
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

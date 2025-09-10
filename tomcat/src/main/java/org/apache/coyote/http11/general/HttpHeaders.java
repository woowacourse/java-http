package org.apache.coyote.http11.general;

import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(String key, String value) {
        this.headers.put(key, value);
    }

    public String getHeaderValueOf(String key) {
        return this.headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

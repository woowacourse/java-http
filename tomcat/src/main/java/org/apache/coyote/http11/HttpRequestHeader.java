package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headers;

    public HttpRequestHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getBodyLength() {
        String value = headers.getOrDefault("Content-Length", "0");
        return Integer.parseInt(value);
    }
}

package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }

    public String getCookies() {
        return headers.get("Cookie");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

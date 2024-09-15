package org.apache.coyote.http11.request;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public RequestHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public boolean existsSession() {
        return headers.containsKey(COOKIE);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public String getCookies() {
        return headers.get(COOKIE);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}

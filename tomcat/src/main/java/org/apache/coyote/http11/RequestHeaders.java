package org.apache.coyote.http11;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public boolean hasNotContent() {
        return !headers.containsKey("Content-Type");
    }

    public String geHeaderValue(final String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> headers;

    public ResponseHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String headerType, String headerValue) {
        headers.put(headerType, headerValue);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

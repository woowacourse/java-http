package org.apache.coyote.dto;

import java.util.Map;

public class HttpHeader {
    private final Map<String,String> headers;

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

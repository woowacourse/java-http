package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String get(String key) {
        return headers.get(key);
    }

    public String contentLength() {
        return headers.getOrDefault("Content-Length", "0");
    }

}

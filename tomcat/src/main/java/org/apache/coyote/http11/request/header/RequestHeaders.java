package org.apache.coyote.http11.request.header;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getOrDefault(String key, String value) {
        return headers.getOrDefault(key, value);
    }
}

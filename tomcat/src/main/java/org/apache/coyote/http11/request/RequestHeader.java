package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getContentType() {
        return headers.get("Accept").split(",")[0];
    }
}

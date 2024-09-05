package org.apache.coyote.http11.request;

import java.util.Map;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public String findHeader(final String name) {
        return headers.getOrDefault(name, name);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }
}

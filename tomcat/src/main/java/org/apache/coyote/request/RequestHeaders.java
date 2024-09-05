package org.apache.coyote.request;

import java.util.Map;

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String contentLength() {
        return headers.get("Content-Length");
    }
}

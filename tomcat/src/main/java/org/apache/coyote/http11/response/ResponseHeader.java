package org.apache.coyote.http11.response;

import java.util.Map;

public class ResponseHeader {

    private final Map<String, String> headers;

    public ResponseHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

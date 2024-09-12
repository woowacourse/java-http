package org.apache.coyote.http11.response;

import java.util.Map;

public class HttpResponseHeader {
    private final Map<String, String> headers;

    public HttpResponseHeader(Map<String, String> headers) {
        this.headers = headers;
    }
}

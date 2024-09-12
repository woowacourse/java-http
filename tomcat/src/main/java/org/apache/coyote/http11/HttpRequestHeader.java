package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequestHeader {
    private final Map<String, String> headers;

    public HttpRequestHeader(Map<String, String> headers) {
        this.headers = headers;
    }
}

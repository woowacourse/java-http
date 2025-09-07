package org.apache.coyote.http11;

import java.util.Map;

public class HttpHeader {

    private final Map<String, String> headers;

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }
}

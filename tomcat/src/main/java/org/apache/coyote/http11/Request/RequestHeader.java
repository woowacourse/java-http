package org.apache.coyote.http11.Request;

import java.util.Map;

public class RequestHeader {

    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public String get(final String key) {
        return headers.get(key);
    }
}

package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseHeaders {

    private final Map<ResponseHeaderType, Object> headers;

    public HttpResponseHeaders() {
        this(new LinkedHashMap<>());
    }

    public HttpResponseHeaders(final Map<ResponseHeaderType, Object> headers) {
        this.headers = headers;
    }

    public void add(final ResponseHeaderType headerType, final Object value) {
        headers.put(headerType, value);
    }

    public Map<ResponseHeaderType, Object> getHeaders() {
        return headers;
    }
}

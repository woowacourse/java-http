package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponseHeaders {

    private final Map<String, String> headers;

    private HttpResponseHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpResponseHeaders getInstance() {
        return new HttpResponseHeaders(new LinkedHashMap<>());
    }

    public void add(final String header, final String value) {
        headers.put(header, value);
    }

    public String get(final String header) {
        return headers.get(header);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

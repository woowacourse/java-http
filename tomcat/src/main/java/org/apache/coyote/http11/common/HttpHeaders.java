package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    private HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders getInstance() {
        return new HttpHeaders(new LinkedHashMap<>());
    }

    public static HttpHeaders from(final Map<String, String> headers) {
        return new HttpHeaders(headers);
    }

    public void addHeader(final String header, final String value) {
        headers.put(header, value);
    }

    public String get(final String header) {
        return headers.get(header);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

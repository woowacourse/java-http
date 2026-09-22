package org.apache.coyote.http11;

import java.util.Map;
import java.util.TreeMap;

public class HttpHeaders {
    private final Map<String, String> headers;

    private HttpHeaders() {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.headers.putAll(headers);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders();
    }
}

package org.apache.coyote.support;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void put(final String key, final String value) {
        headers.put(key, value);
    }

    public String getValueOrDefault(final String key, final String defaultValue) {
        return headers.getOrDefault(key, defaultValue);
    }

    public HttpHeaders toResponse() {
        HashMap<String, String> headers = new HashMap<>(this.headers);
        String cookieValue = headers.get(HttpHeader.COOKIE.getValue());
        if (cookieValue != null) {
            headers.put(HttpHeader.SET_COOKIE.getValue(), cookieValue);
            headers.remove(HttpHeader.COOKIE.getValue());
            return new HttpHeaders(headers);
        }
        return new HttpHeaders(headers);
    }
}

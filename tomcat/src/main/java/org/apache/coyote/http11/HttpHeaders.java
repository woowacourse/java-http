package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String COOKIE = "Cookie";

    private final Map<String, String> headers = new LinkedHashMap<>();

    public boolean containsHeader(String headerName) {
        return this.headers.containsKey(headerName);
    }

    public void put(String key, String value) {
        headers.put(key, value);
    }

    public String get(String headerName) {
        return headers.get(headerName);
    }

    public void putAll(final Map<String, String> headers) {
        this.headers.putAll(headers);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

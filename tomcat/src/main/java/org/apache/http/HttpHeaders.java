package org.apache.http;

import java.util.HashMap;
import java.util.Map;

public final class HttpHeaders {

    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public void add(final String headerName, final String headerValue) {
        headers.put(headerName, headerValue);
    }

    public Map<String, String> getAllHeaders() {
        return new HashMap<>(headers);
    }

    public boolean containsHeader(final String headerName) {
        return headers.containsKey(headerName);
    }

    public String getHeaderValue(final String headerName) {
        return headers.get(headerName);
    }
}

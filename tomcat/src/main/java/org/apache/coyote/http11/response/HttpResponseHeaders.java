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

    public void addContentType(final Object value) {
        headers.put(ResponseHeaderType.CONTENT_TYPE, value);
    }

    public void addLocation(final Object value) {
        headers.put(ResponseHeaderType.LOCATION, value);
    }

    public void addCookie(final Object value) {
        headers.put(ResponseHeaderType.SET_COOKIE, value);
    }

    public void addContentLength(final Object value) {
        headers.put(ResponseHeaderType.CONTENT_LENGTH, value);
    }

    public Map<ResponseHeaderType, Object> getHeaders() {
        return headers;
    }
}

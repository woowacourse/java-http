package org.apache.coyote.support;

import java.util.Map;

public class HttpHeaders {

    private static final String CONTENT_LENGTH_DEFAULT_VALUE = "0";
    private static final String COOKIE_DEFAULT_VALUE = "";

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setContentLength(final String length) {
        headers.put(HttpHeader.CONTENT_LENGTH.getValue(), length);
    }

    public String getContentLength() {
        return headers.getOrDefault(HttpHeader.CONTENT_LENGTH.getValue(), CONTENT_LENGTH_DEFAULT_VALUE);
    }

    public String getCookie() {
        return headers.getOrDefault(HttpHeader.COOKIE.getValue(), COOKIE_DEFAULT_VALUE);
    }
}

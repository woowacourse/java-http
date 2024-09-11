package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHeaders {
    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String LOCATION = "Location";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setCookie(String cookie) {
        headers.put(SET_COOKIE, cookie);
    }

    public void setLocation(String location) {
        headers.put(LOCATION, location);
    }

    public void setContentType(String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void setContentLength(int length) {
        headers.put(CONTENT_LENGTH, String.valueOf(length));
    }

    public boolean haveContentLength() {
        return Objects.nonNull(headers.get(CONTENT_LENGTH));
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get(CONTENT_LENGTH));
    }

    public String getCookie() {
        return headers.get(COOKIE);
    }

    public Map<String, String> getHeaders() {
        return Map.copyOf(headers);
    }
}

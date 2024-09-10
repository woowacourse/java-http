package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHeaders {
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String LOCATION = "Location";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";

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

package com.techcourse.http;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class HttpHeaders {

    private static final String HEADER_SEPARATOR = ": ";
    private static final String CRLF = "\r\n";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String SET_COOKIE_HEADER = "Set-Cookie";
    public static final String COOKIE = "Cookie";
    public static final String LOCATION = "Location";
    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final Map<String, String> headers;
    private final HttpCookie cookie;

    public HttpHeaders() {
        this.headers = new HashMap<>();
        this.cookie = new HttpCookie();
    }

    public void clear() {
        headers.clear();
        cookie.clear();
    }

    public String getHeadersString() {
        if (cookie.isExist()) {
            headers.put(SET_COOKIE_HEADER, cookie.serialize());
        }
        StringBuilder headersString = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headersString.append(entry.getKey())
                    .append(HEADER_SEPARATOR)
                    .append(entry.getValue())
                    .append(" ")
                    .append(CRLF);
        }
        headersString.append(cookie.serialize());
        return headersString.toString();
    }

    public String get(String key) {
        return headers.get(key);
    }

    public void set(String key, String value) {
        headers.put(key, value);
    }

    public void setContentType(String contentType) {
        set(CONTENT_TYPE, contentType);
    }

    public void setContentLength(int contentLength) {
        set(CONTENT_LENGTH, String.valueOf(contentLength));
    }

    public void setCookie(String cookieKey, String cookieValue) {
        cookie.setCookie(cookieKey, cookieValue);
    }

    public void setLocation(String location) {
        set(LOCATION, location);
    }

    public String getCookie(String key) {
        return cookie.getCookie(key);
    }

    public void setHttpOnly(boolean httpOnly) {
        cookie.setHttpOnly(httpOnly);
    }
}

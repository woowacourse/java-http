package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> headers;

    public HttpRequestHeader() {
        this.headers = new HashMap<>();
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getBodyLength() {
        String value = headers.getOrDefault("Content-Length", "0");
        String trimmed = value.trim();
        return Integer.parseInt(trimmed);
    }

    public HttpCookie getCookie() {
        String cookie = headers.getOrDefault("Cookie", "");
        if (cookie.isBlank()) {
            return new HttpCookie();
        }
        String[] split = cookie.split(";");

        Map<String, String> cookies = new HashMap<>();
        for (String value : split) {
            String trimmed = value.trim();
            String[] keyValue = trimmed.split("=");
            cookies.put(keyValue[0], keyValue[1]);
        }
        return new HttpCookie(cookies);
    }
}

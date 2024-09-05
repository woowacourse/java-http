package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String COOKIE = "Cookie";

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new LinkedHashMap<>(headers);
    }

    public String buildHttpHeadersResponse() {
        return String.join("\r\n",
                headers.entrySet().stream()
                        .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                        .toArray(String[]::new)
        );
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}

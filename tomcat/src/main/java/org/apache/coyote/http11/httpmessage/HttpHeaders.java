package org.apache.coyote.http11.httpmessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String LOCATION = "Location";
    public static final String COOKIE = "Cookie";
    public static final String JSESSIONID = "JSESSIONID";
    public static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpHeaders() {
        this(new HashMap<>());
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public int getContentLength() {
        return Integer.parseInt(
                Optional.ofNullable(headers.get("Content-Length"))
                        .orElse("0")
        );
    }

    public String toHttpMessage() {
        return headers.entrySet().stream()
                .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                headers.entrySet().stream()
                        .map(entry -> String.format("\n\t\t%s %s", entry.getKey(), entry.getValue()))
                        .collect(Collectors.joining()) +
                '}';
    }
}

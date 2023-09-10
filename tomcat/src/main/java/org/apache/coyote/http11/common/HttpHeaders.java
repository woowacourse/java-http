package org.apache.coyote.http11.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private final Map<String, String> headers;
    private final HttpCookie httpCookie;

    public HttpHeaders(Map<String, String> headers, HttpCookie httpCookie) {
        this.headers = headers;
        this.httpCookie = httpCookie;
    }

    public static HttpHeaders createRequestHeaders(Map<String, String> headers) {
        String cookie = headers.remove("Cookie");

        return new HttpHeaders(headers, HttpCookie.from(cookie));
    }

    public static HttpHeaders createResponseHeaders() {
        return new HttpHeaders(new LinkedHashMap<>(), HttpCookie.create());
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addCookie(String key, String value) {
        httpCookie.put(key, value);
    }

    public void addCookies(HttpCookie httpCookie) {
        this.httpCookie.putAll(httpCookie);
    }

    public String get(String key) {
        return headers.get(key);
    }

    public String findCookie(String key) {
        return httpCookie.find(key);
    }

    public String findJSessionId() {
        return httpCookie.findJSessionId();
    }

    public StringBuilder convertResponseHeaders() {
        return new StringBuilder()
                    .append(httpCookie.getCookies())
                    .append(getHeaders());
    }

    private String getHeaders() {
        return headers.entrySet().stream()
                .map(this::formatHeaders)
                .collect(Collectors.joining("\r\n", "", " "));
    }

    private String formatHeaders(Map.Entry<String, String> entry) {
        return String.join(": ", entry.getKey(), entry.getValue());
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + headers +
                ", httpCookie=" + httpCookie +
                '}';
    }
}

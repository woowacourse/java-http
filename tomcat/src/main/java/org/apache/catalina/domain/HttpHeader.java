package org.apache.catalina.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.domain.cookie.HttpCookie;
import org.apache.catalina.domain.cookie.HttpCookies;

public record HttpHeader(Map<String, String> headers) {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";

    public HttpHeader() {
        this(new LinkedHashMap<>());
    }

    public static HttpHeader from(List<String> requestLines) {
        Map<String, String> headers = new LinkedHashMap<>();
        requestLines.stream()
                .skip(1)
                .forEach(line -> putHeader(headers, line));

        return new HttpHeader(headers);
    }

    private static void putHeader(Map<String, String> headers, String line) {
        String[] parts = line.split(": ", 2);

        if (parts.length != 2) {
            return;
        }
        String key = normalizeHeaderKey(parts[0].trim());
        String value = parts[1].trim();

        if (headers.containsKey(key)) {
            headers.compute(key, (k, existingValue) -> existingValue + ", " + value);
            return;
        }

        headers.put(key, value);
    }

    private static String normalizeHeaderKey(String key) {
        String[] tokens = key.split("-");
        StringBuilder builder = new StringBuilder();
        for (String token : tokens) {
            builder.append(Character.toUpperCase(token.charAt(0)))
                    .append(token.substring(1).toLowerCase())
                    .append("-");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public void put(String key, String value) {
        final String headerKey = normalizeHeaderKey(key);
        if (headers.containsKey(headerKey)) {
            headers.compute(headerKey, (k, existingValue) -> existingValue + ", " + value);
            return;
        }

        headers.put(headerKey, value);
    }

    public String get(String key) {
        return headers.get(normalizeHeaderKey(key));
    }

    public boolean containKey(String key) {
        return headers.keySet().stream()
                .anyMatch(k -> normalizeHeaderKey(k).equals(normalizeHeaderKey(key)));
    }

    public int getContentLength() {
        if (!containKey(CONTENT_LENGTH)) {
            return 0;
        }
        return Integer.parseInt(get(CONTENT_LENGTH));
    }

    public String getContentType() {
        if (!containKey(CONTENT_LENGTH)) {
            throw new IllegalArgumentException("Content-Length header is missing");
        }
        return get(CONTENT_TYPE);
    }

    public boolean hasCookie() {
        return containKey(COOKIE);
    }

    public HttpCookies getCookies() {
        if (hasCookie()) {
            return HttpCookies.from(get(COOKIE));
        }

        return new HttpCookies();
    }

    public void addSetCookie(HttpCookie cookie) {
        final String headerKey = normalizeHeaderKey(SET_COOKIE);
        if (headers.containsKey(headerKey)) {
            headers.compute(headerKey, (k, existingValue) -> existingValue + ", " + cookie.toString());
            return;
        }
        headers.put(headerKey, cookie.toString());
    }
}

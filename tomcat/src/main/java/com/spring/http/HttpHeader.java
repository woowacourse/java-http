package com.spring.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.spring.http.cookie.HttpCookies;

public final class HttpHeader {

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private final Map<String, String> headers;

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

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
        String[] parts = line.split(":", 2);

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

    public boolean hasNotContentLength() {
        return !containKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        if (!containKey(CONTENT_LENGTH)) {
            return 0;
        }
        return Integer.parseInt(get(CONTENT_LENGTH));
    }

    public void setContentLength(byte[] bytes){
        put(CONTENT_LENGTH, String.valueOf(bytes.length));
    }

    public String getContentType() {
        if (!containKey(CONTENT_TYPE)) {
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


    public String toHeaderString() {
        StringBuilder builder = new StringBuilder();
        headers.forEach((key, value) ->
                builder.append(key).append(": ").append(value).append("\r\n"));
        return builder.toString();
    }

    @Override
    public String toString() {
        return "HttpHeader[" +
                "headers=" + headers + ']';
    }

}

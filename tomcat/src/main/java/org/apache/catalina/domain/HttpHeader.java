package org.apache.catalina.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record HttpHeader(Map<String, String> headers) {

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

    public boolean isEmpty() {
        return headers.isEmpty();
    }

    public boolean containsKey(String key) {
        return containKey(key);
    }
}

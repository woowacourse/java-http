package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record HttpHeaders(Map<String, String> headers) {

    public static HttpHeaders from(final List<String> rawHeaders) {
        final Map<String, String> headers = new HashMap<>();

        for (final String headerLine : rawHeaders) {
            final String[] parsedHeader = headerLine.split(":\\s+", 2);
            headers.put(parsedHeader[0].trim(), parsedHeader[1].trim());
        }

        return new HttpHeaders(headers);
    }

    public String get(final String name) {
        return headers.get(name);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    public void add(final String key, final String value) {
        headers.put(key, value);
    }

    @Override
    public String toString() {
        final StringBuilder response = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            response.append(entry.getKey()).append(": ");
            response.append(entry.getValue()).append(" ");
            response.append("\r\n");
        }
        return response.toString();
    }
}

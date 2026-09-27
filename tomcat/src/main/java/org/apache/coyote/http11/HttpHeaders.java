package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record HttpHeaders(Map<String, String> headers) {

    public static HttpHeaders from(final List<String> rawHeaders) {
        final Map<String, String> headers = new HashMap<>();

        for (final String headerLine : rawHeaders) {
            final String[] parsedHeader = headerLine.split(":\\s+", 2);
            headers.put(parsedHeader[0].trim().toLowerCase(), parsedHeader[1].trim());
        }

        return new HttpHeaders(headers);
    }

    public String get(final String name) {
        return headers.get(name);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("content-length", "0"));
    }
}

package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, List<String>> headers = new LinkedHashMap<>();
    private final Map<String, String> originalKeys = new LinkedHashMap<>();

    public void addHeader(final String key, final String value) {
        final var normalizedKey = key.toLowerCase();
        this.originalKeys.putIfAbsent(normalizedKey, key);
        this.headers.computeIfAbsent(normalizedKey, k -> new ArrayList<>())
                .add(value);
    }

    public String toHeaderString() {
        final StringBuilder headerString = new StringBuilder();
        for (final Map.Entry<String, List<String>> entry : headers.entrySet()) {
            final var normalizedKey = entry.getKey();
            final var originalKey = this.originalKeys.get(normalizedKey);
            for (final String value : entry.getValue()) {
                headerString.append(originalKey)
                        .append(": ")
                        .append(value)
                        .append("\r\n");
            }
        }
        return headerString.toString();
    }
}

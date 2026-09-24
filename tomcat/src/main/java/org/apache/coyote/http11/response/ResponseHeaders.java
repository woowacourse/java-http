package org.apache.coyote.http11.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ResponseHeaders {

    private final Map<String, List<String>> headers = new HashMap<>();

    public void addHeader(final String name, final String value) {
        final String normalizedName = name.toLowerCase(Locale.ROOT);

        headers.computeIfAbsent(
                normalizedName,
                key -> new ArrayList<>()
        ).add(value);
    }

    public void setHeader(final String name, final String value) {
        final String normalizedName = name.toLowerCase(Locale.ROOT);

        headers.put(
                normalizedName,
                new ArrayList<>(List.of(value))
        );
    }

    public String getHeader(final String name) {
        final String normalizedName = name.toLowerCase(Locale.ROOT);
        final List<String> values = headers.get(normalizedName);

        if (values == null || values.isEmpty()) {
            return null;
        }

        return values.getFirst();
    }

    public String toResponseHeaders() {
        final StringBuilder result = new StringBuilder();

        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String value : entry.getValue()) {
                result.append(entry.getKey())
                        .append(": ")
                        .append(value)
                        .append("\r\n");
            }
        }

        return result.toString();
    }
}

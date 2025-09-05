package org.apache.coyote.http11;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void addHeader(
            final String name,
            final String value
    ) {
        headers.put(name, value);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(", ");
        }

        return sb.toString();
    }
}

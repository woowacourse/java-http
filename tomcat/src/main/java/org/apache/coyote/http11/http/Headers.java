package org.apache.coyote.http11.http;

import java.util.LinkedHashMap;
import java.util.Map;

public record Headers(Map<String, String> headers) {

    private static final String DELIMITER = ": ";

    public Headers() {
        this(new LinkedHashMap<>());
    }

    public String get(final String name) {
        return headers.get(name);
    }

    public void put(final String headerLine) {
        if (!headerLine.contains(DELIMITER)) {
            throw new IllegalArgumentException(headerLine);
        }
        final var split = headerLine.split(DELIMITER);
        headers.remove(split[0]);
        add(split[0], split[1]);
    }

    public Headers add(final String name, final String value) {
        if (headers.containsKey(name)) {
            headers.merge(name, value, (v1, v2) -> v1 + "," + v2);
            return this;
        }
        headers.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        final var result = new StringBuilder();
        for (String key : headers.keySet()) {
            result.append(key)
                    .append(": ")
                    .append(headers.get(key))
                    .append(" \r\n");
        }
        return result.toString();
    }
}

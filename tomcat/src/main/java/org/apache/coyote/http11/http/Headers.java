package org.apache.coyote.http11.http;

import java.util.LinkedHashMap;
import java.util.Map;

public record Headers(Map<String, String> headers) {

    private static final String DELIMITER = ": ";

    public Headers() {
        this(new LinkedHashMap<>());
    }

    public void put(final String headerLine) {
        final var split = headerLine.split(DELIMITER);
        put(split[0], split[1]);
    }

    public void put(final String name, final String value) {
        if (headers.containsKey(name)) {
            headers.merge(name, value, (v1, v2) -> v1 + ";" + v2);
            return;
        }
        headers.put(name, value);
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

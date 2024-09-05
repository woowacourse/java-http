package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;

public record Headers(Map<String, String> headers) {

    private static final String DELIMITER = ": ";

    public Headers() {
        this(new HashMap<>());
    }

    public void put(final String header) {
        final var split = header.split(DELIMITER);
        put(split[0], split[1]);
    }

    public void put(final String name, final String value) {
        headers.put(name, value);
    }
}

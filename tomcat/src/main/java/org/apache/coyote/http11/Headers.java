package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Headers {

    private final Map<String, String> headers = new HashMap<>();

    public void add(final String header) {
        final int separator = header.indexOf(':');
        if (separator <= 0) {
            throw new IllegalArgumentException("잘못된 헤더: " + header);
        }

        final String name = header.substring(0, separator).trim();
        final String value = header.substring(separator + 1).trim();
        headers.put(name, value);
    }

}

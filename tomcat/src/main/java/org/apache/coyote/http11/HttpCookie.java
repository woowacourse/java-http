package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> values = new HashMap<>();

    public HttpCookie(final String header) {
        if (header == null || header.isBlank()) {
            return;
        }

        for (String pair : header.split(";")) {
            int equalsIndex = pair.indexOf('=');
            if (equalsIndex < 0) {
                continue;
            }

            String name = pair.substring(0, equalsIndex).trim();
            String value = pair.substring(equalsIndex + 1).trim();
            if (!name.isEmpty()) {
                values.put(name, value);
            }
        }
    }

    public boolean has(final String name) {
        String value = values.get(name);
        return value != null && !value.isBlank();
    }

    public String get(final String name) {
        return values.get(name);
    }
}

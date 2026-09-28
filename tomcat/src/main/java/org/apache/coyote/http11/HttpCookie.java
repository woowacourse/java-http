package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public final class HttpCookie {

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie parse(final String cookieHeader) {
        final Map<String, String> values = new HashMap<>();

        if (cookieHeader != null) {
            for (final var cookie : cookieHeader.split(";")) {
                final var nameAndValue = cookie.trim().split("=", 2);
                if (nameAndValue.length == 2) {
                    values.put(nameAndValue[0], nameAndValue[1]);
                }
            }
        }

        return new HttpCookie(values);
    }

    public String get(final String name) {
        return values.get(name);
    }
}

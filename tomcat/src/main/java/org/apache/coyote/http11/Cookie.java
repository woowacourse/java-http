package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_SEPARATOR = ";";
    private static final String VALUE_SEPARATOR = "=";

    private final Map<String, String> values;

    private Cookie(final Map<String, String> values) {
        this.values = values;
    }

    public static Cookie from(final String cookieHeader) {
        final Map<String, String> values = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return new Cookie(values);
        }

        for (final String cookie : cookieHeader.split(COOKIE_SEPARATOR)) {
            final String[] keyValue = cookie.trim().split(VALUE_SEPARATOR, 2);
            if (keyValue.length == 2 && !keyValue[0].isBlank()) {
                values.put(keyValue[0], keyValue[1]);
            }
        }
        return new Cookie(values);
    }

    public String getValue(final String name) {
        return values.get(name);
    }

    public String getJSessionId() {
        return values.get(JSESSIONID);
    }

    public boolean hasJSessionId() {
        return values.containsKey(JSESSIONID);
    }
}

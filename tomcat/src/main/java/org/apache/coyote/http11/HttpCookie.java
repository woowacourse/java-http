package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public record HttpCookie(
        Map<String, String> values
) {

    private static final String COOKIE_DELIMITER = ";";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_PARTS_COUNT = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static HttpCookie empty() {
        return new HttpCookie(Map.of());
    }

    public static HttpCookie from(final String cookies) {
        final Map<String, String> values = new HashMap<>();

        for (String cookie : cookies.split(COOKIE_DELIMITER)) {
            final String trimmed = cookie.trim();

            if (trimmed.isEmpty()) {
                continue;
            }

            final String[] keyValue = trimmed.split(KEY_VALUE_DELIMITER, KEY_VALUE_PARTS_COUNT);
            String value = "";

            if (keyValue.length == KEY_VALUE_PARTS_COUNT) {
                value = keyValue[VALUE_INDEX].trim();
            }

            values.putIfAbsent(keyValue[KEY_INDEX], value);
        }

        return new HttpCookie(Map.copyOf(values));
    }

    public boolean contains(final String name) {
        return values.containsKey(name);
    }

    public String get(final String name) {
        return values.get(name);
    }
}

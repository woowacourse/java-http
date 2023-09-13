package org.apache.coyote.http11.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_NAME_VALUE_DELIMITER = "=";

    private final Map<String, String> valuesByName;

    public Cookie(final String name, final String value) {
        this.valuesByName = new HashMap<>();
        valuesByName.put(name, value);
    }

    public Cookie(final Map<String, String> valuesByName) {
        this.valuesByName = valuesByName;
    }

    public static Cookie fromHeaderCookie(final String headerCookieLine) {
        final Map<String, String> namesWithValue = Arrays.stream(headerCookieLine.split(COOKIE_DELIMITER))
            .map(cookieWithValue -> cookieWithValue.split(COOKIE_NAME_VALUE_DELIMITER))
            .collect(Collectors.toMap(cookiePair -> cookiePair[0], cookiePair -> cookiePair[1]));
        return new Cookie(namesWithValue);
    }

    public String findByName(final String name) {
        return valuesByName.get(name);
    }

    public String getAllNamesWithValue() {
        return valuesByName.entrySet()
            .stream()
            .map(entry -> String.join(COOKIE_NAME_VALUE_DELIMITER, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}

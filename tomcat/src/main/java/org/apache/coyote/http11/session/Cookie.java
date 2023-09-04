package org.apache.coyote.http11.session;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_NAME_VALUE_DELIMITER = "=";

    private final Map<String, String> namesWithValue;

    public Cookie(final String name, final String value) {
        this.namesWithValue = new HashMap<>();
        namesWithValue.put(name, value);
    }

    public Cookie(final Map<String, String> namesWithValue) {
        this.namesWithValue = namesWithValue;
    }

    public static Cookie fromHeaderCookie(final String headerCookieLine) {
        final Map<String, String> namesWithValue = Arrays.stream(headerCookieLine.split(COOKIE_DELIMITER))
            .map(cookieWithValue -> cookieWithValue.split(COOKIE_NAME_VALUE_DELIMITER))
            .collect(
                Collectors.toMap(cookieValue -> cookieValue[0], cookieValue -> cookieValue[1])
            );
        return new Cookie(namesWithValue);
    }

    public void create(final String name, final String value) {
        namesWithValue.put(name, value);
    }

    public String findNameWithValueByName(final String name) {
        return String.join(COOKIE_NAME_VALUE_DELIMITER, name, namesWithValue.get(name));
    }

    public String getAllNamesWithValue() {
        return namesWithValue.entrySet()
            .stream()
            .map(entry -> String.join(COOKIE_NAME_VALUE_DELIMITER, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(COOKIE_DELIMITER));
    }
}

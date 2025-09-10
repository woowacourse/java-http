package org.apache.coyote.http11;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Cookies {

    private static final String COOKIE_DELIMITER = "; =";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookies;

    public Cookies(String cookieHeader) {
        this.cookies = parse(cookieHeader);
    }

    private Map<String, String> parse(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return Map.of();
        }
        return Stream.of(cookieHeader.split(COOKIE_DELIMITER))
                .map(String::trim)
                .map(cookie -> cookie.split(KEY_VALUE_DELIMITER, 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }
}

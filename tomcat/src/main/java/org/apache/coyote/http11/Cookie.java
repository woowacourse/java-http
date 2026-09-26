package org.apache.coyote.http11;

import java.util.Optional;

public record Cookie(String name, String value) {
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_SIZE = 2;

    public static Optional<Cookie> fromCookiePair(String cookiePair) {
        String[] cookiePairParts = cookiePair.strip().split(KEY_VALUE_DELIMITER, KEY_VALUE_SIZE);
        if (cookiePairParts.length != KEY_VALUE_SIZE) {
            return Optional.empty();
        }
        return Optional.of(new Cookie(cookiePairParts[0].strip(), cookiePairParts[1].strip()));
    }

    public String toHeaderValue() {
        return name + KEY_VALUE_DELIMITER + value;
    }
}

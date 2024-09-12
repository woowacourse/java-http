package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpCookies {

    private static final String DELIMITER_SEMICOLON = "; ";

    private final Map<String, String> values = new HashMap<>();

    public void put(String key, String value) {
        values.put(key, value);
    }

    public String get(String key) {
        return values.get(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public String toMessage() {
        return values.entrySet().stream()
                .map(Entry::toString)
                .collect(Collectors.joining(DELIMITER_SEMICOLON));
    }
}

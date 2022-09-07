package org.apache.coyote.http.response;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseCookie {

    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String FIELD_DELIMITER = "; ";

    private final Map<String, String> cookies;

    public ResponseCookie() {
        this.cookies = new HashMap<>();
    }

    public void add(final String key, final String value) {
        cookies.put(key, value);
    }

    public boolean hasValue() {
        return !cookies.isEmpty();
    }

    public String toHeaderForm() {
        return cookies.entrySet()
                .stream()
                .map(it -> it.getKey() + KEY_VALUE_DELIMITER + it.getValue())
                .collect(Collectors.joining(FIELD_DELIMITER));
    }
}

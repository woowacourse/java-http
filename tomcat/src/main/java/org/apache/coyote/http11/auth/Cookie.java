package org.apache.coyote.http11.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.EMPTY_MAP;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class Cookie {

    private static final String DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> elements = new HashMap<>();

    private Cookie(final Map<String, String> elements) {
        this.elements.putAll(elements);
    }

    public static Cookie from(final List<String> elements) {
        if (elements == null) {
            return new Cookie(EMPTY_MAP);
        }
        return elements.stream()
                .map(header -> header.split(DELIMITER))
                .collect(collectingAndThen(
                        toMap(header -> header[KEY_INDEX], header -> header[VALUE_INDEX]),
                        Cookie::new
                ));
    }

    public static Cookie createSessionCookie() {
        Map<String, String> cookie = new HashMap<>();
        final String uuid = UUID.randomUUID().toString();
        cookie.put("JSESSIONID", uuid);
        return new Cookie(cookie);
    }

    public void put(final String key, final String value) {
        elements.put(key, value);
    }

    public String get(final String key) {
        return elements.get(key);
    }

}

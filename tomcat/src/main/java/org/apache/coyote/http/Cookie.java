package org.apache.coyote.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cookie {

    public static final String COOKIE_FIELD_DELIMITER = "=";
    private static final String COOKIE_DATA_DELIMITER = "; ";
    private static final int SPLIT_LIMIT = 2;

    private final Map<String, String> cookies;

    public Cookie(String cookies) {
        this.cookies = Arrays.stream(cookies.split(COOKIE_DATA_DELIMITER))
                .map(cookie -> cookie.split(COOKIE_FIELD_DELIMITER, SPLIT_LIMIT))
                .filter(cookie -> cookie.length == SPLIT_LIMIT)
                .collect(HashMap::new,
                         (map, entry) -> map.put(entry[0], entry[1]),
                         HashMap::putAll);
    }

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie empty() {
        return new Cookie(Map.of());
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(cookies.get(key));
    }

    @Override
    public String toString() {
        return "Cookie{" +
               "cookies=" + cookies +
               '}';
    }
}

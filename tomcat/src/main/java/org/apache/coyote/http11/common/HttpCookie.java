package org.apache.coyote.http11.common;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String SEPARATOR = "; ";
    private static final String DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> items = new HashMap<>();

    public HttpCookie() {
    }

    private HttpCookie(final Map<String, String> items) {
        this.items.putAll(items);
    }

    public static HttpCookie from(final String cookie) {
        if (cookie.isEmpty()) {
            return new HttpCookie();
        }
        return Arrays.stream(cookie.split(SEPARATOR))
                .map(header -> header.split(DELIMITER))
                .collect(collectingAndThen(
                        toMap(header -> header[KEY_INDEX], header -> header[VALUE_INDEX]),
                        HttpCookie::new
                ));
    }

    public void put(final String key, final String value) {
        items.put(key, value);
    }

    public String get(final String key) {
        return items.get(key);
    }

    public Map<String, String> getItems() {
        return items;
    }
}

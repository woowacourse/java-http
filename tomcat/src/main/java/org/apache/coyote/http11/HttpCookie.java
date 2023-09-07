package org.apache.coyote.http11;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String DELIMITER = "; ";
    private static final String EQUAL = "=";

    private final Map<String, String> items;

    public HttpCookie() {
        this.items = new HashMap<>();
    }

    private HttpCookie(final Map<String, String> items) {
        this.items = items;
    }

    public static HttpCookie from(final String cookieHeader) {
        if (cookieHeader.isEmpty()) {
            return new HttpCookie();
        }
        return Arrays.stream(cookieHeader.split(DELIMITER))
                .map(it -> it.split(EQUAL, 2))
                .collect(collectingAndThen(
                        toMap(it -> it[0], it -> it[1]),
                        HttpCookie::new
                ));
    }

    public void setCookie(final String key, final String value) {
        items.put(key, value);
    }

    public boolean contains(final String key) {
        return items.containsKey(key);
    }

    public String getCookie(final String key) {
        return items.get(key);
    }

    @Override
    public String toString() {
        return items.entrySet()
                .stream()
                .map(entry -> entry.getKey() + EQUAL + entry.getValue())
                .collect(Collectors.joining(DELIMITER));
    }
}

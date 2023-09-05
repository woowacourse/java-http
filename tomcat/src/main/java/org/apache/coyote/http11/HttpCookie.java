package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class HttpCookie {

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
        return Arrays.stream(cookieHeader.split("; "))
                .map(it -> it.split("="))
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
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}

package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public HttpCookie(String rawCookies) {
        this.cookies = parse(rawCookies);
    }

    private Map<String, String> parse(String rawCookies) {
        if (rawCookies == null) {
            return Collections.emptyMap();
        }

        return Arrays.stream(rawCookies.split("; "))
                .map(cookie -> cookie.split("="))
                .filter(data -> data.length == 2)
                .collect(Collectors.toMap(data -> data[0], data -> data[1]));
    }

    public void add(String name, String value) {
        cookies.put(name, value);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public String buildMessage() {
        return cookies.keySet().stream()
                .map(key -> key + "=" + cookies.get(key))
                .collect(Collectors.joining("; "));
    }

    public boolean contains(String name) {
        return cookies.containsKey(name);
    }
}

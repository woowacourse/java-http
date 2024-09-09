package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(final String values) {
        this.cookies = Arrays.stream(values.split("; "))
                .map(token -> token.split("="))
                .collect(Collectors.toMap(value -> value[0], value -> value[1]));
    }

    public boolean contains(final String key) {
        return cookies.containsKey(key);
    }

    public String getCookieValue(final String key) {
        return cookies.get(key);
    }
}

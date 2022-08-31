package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    @Override
    public String toString() {
        return cookies.entrySet().stream()
            .map(it -> it.getKey() + "=" + it.getValue())
            .collect(Collectors.joining(";"));
    }
}


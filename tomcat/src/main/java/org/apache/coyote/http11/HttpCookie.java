package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie() {
        this.cookie = new LinkedHashMap<>();
    }

    public void append(String key, String value) {
        this.cookie.put(key, value);
    }

    public boolean isExists(String key) {
        return cookie.containsKey(key);
    }

    public String getValue(String key) {
        return cookie.get(key);
    }

    public boolean isEmpty() {
        return cookie.isEmpty();
    }

    public String getCookieMessage() {
        return cookie.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));
    }
}

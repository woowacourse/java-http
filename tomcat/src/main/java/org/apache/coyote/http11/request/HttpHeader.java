package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

    private static final String COOKIE_KEY = "Cookie";

    private final Map<String, String> value = new HashMap<>();

    public void put(final String key, final String value) {
        this.value.put(key, value);
    }

    public boolean containsKey(final String key) {
        return this.value.containsKey(key);
    }

    public boolean containsJsessionId() {
        if (!containsKey(COOKIE_KEY)) {
            return false;
        }
        final String cookieValue = value.get(COOKIE_KEY);
        return Arrays.stream(cookieValue.split("; "))
                .anyMatch(it -> it.split("=")[0].equals("JSESSIONID"));
    }

    public String get(final String key) {
        return this.value.get(key);
    }
}

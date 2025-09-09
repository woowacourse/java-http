package org.apache.coyote.http11.vo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie() {
        this.cookies = new HashMap<>();
    }

    public HttpCookie(String cookieString) {
        final Map<String, String> result = new HashMap<>();
        final var split = cookieString.split(";");
        for (String pairString : split) {
            final var pairs = Arrays.stream(pairString.split("=")).toList();

            validatePairs(pairs);

            final var key = pairs.getFirst().trim();
            var value = pairs.getLast().trim();
            if (pairs.size() == 1 && pairString.indexOf("=") == pairString.length() - 1) {
                value = "";
            }
            result.put(key, value);
        }
        this.cookies = result;
    }

    private void validatePairs(List<String> pairs) {
        if (pairs.size() >= 3 || pairs.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    public void add(final String key, final String value) {
        cookies.put(key, value);
    }

    public boolean containsKey(final String key) {
        return cookies.containsKey(key);
    }

    public String get(final String key) {
        return cookies.get(key);
    }

    @Override
    public String toString() {
        List<String> pairs = new ArrayList<>();
        for (String key : cookies.keySet()) {
            pairs.add(String.format("%s=%s", key, cookies.get(key)));
        }
        return String.join("; ", pairs);
    }
}

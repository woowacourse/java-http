package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {

    private final Map<String, String> cookieValues;

    public Cookie() {
        this.cookieValues = new HashMap<>();
    }

    public Cookie(String rawCookies) {
        this.cookieValues = new HashMap<>();

        String[] pairs = rawCookies.split("; ");
        for (String pair : pairs) {
            String[] splitPair = pair.split("=");
            String key = splitPair[0];
            String value = splitPair[1];
            cookieValues.put(key, value);
        }
    }

    public String parseToString() {
        return this.cookieValues.keySet().stream()
                .map(key -> key + "=" + cookieValues.get(key))
                .collect(Collectors.joining("; "));
    }

    public void add(String key, String value) {
        this.cookieValues.put(key, value);
    }

    public boolean hasSession() {
        return this.cookieValues.keySet().stream()
                .anyMatch(key -> key.equals("JSESSIONID"));
    }
}

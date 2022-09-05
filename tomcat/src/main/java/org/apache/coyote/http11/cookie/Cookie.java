package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookie {
    private final Map<String, String> cookies;

    public Cookie() {
        this.cookies = new HashMap<>();
    }

    public boolean hasCookie(String cookieName) {
        return cookies.containsKey(cookieName);
    }

    public void setCookie(String cookieKey, String cookieValue) {
        cookies.put(cookieKey, cookieValue);
    }

    public boolean isEmpty() {
        return cookies.isEmpty();
    }

    public String generateCookieEntries() {
        List<String> cookieEntries = cookies.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.toList());

        return String.join("; ", cookieEntries);
    }
}

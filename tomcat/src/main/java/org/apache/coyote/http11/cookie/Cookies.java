package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {

    private final Map<String, Cookie> cookies = new HashMap<String, Cookie>();

    public boolean hasCookie(String key) {
        return cookies.containsKey(key);
    }

    public void addCookie(Cookie cookie) {
        cookies.put(cookie.getKey(), cookie);
    }

    public String toString() {
        return cookies.values().stream()
                .map(Cookie::toString)
                .collect(Collectors.joining("; "));
    }
}

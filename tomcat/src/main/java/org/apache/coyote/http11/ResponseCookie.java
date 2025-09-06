package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public void set(final String name, final String value) {
        cookies.put(name, value);
    }

    public List<String> toSetCookieHeaders() {
        return cookies.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue() + "; Path=/; HttpOnly; SameSite=Lax")
                .toList();
    }
}

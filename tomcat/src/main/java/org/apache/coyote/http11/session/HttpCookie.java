package org.apache.coyote.http11.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return;
        }
        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String trimmed = pair.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            String[] kv = trimmed.split("=", 2);
            if (kv.length == 2) {
                cookies.put(kv[0], kv[1]);
            } else if (kv.length == 1) {
                cookies.put(kv[0], "");
            }
        }
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(cookies);
    }
}

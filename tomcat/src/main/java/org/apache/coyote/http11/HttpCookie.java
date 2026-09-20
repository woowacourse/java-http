package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {
    private final Map<String, String> cookies;

    public HttpCookie(String rawCookie) {
        this.cookies = parse(rawCookie);
    }

    private Map<String, String> parse(String rawCookie) {
        final Map<String, String> map = new HashMap<>();
        if(rawCookie == null || rawCookie.isBlank()) {
            return map;
        }

        String[] pairs = rawCookie.split("; ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2) {
                map.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return map;
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public boolean hasCookie(String name) {
        return cookies.containsKey(name);
    }
}

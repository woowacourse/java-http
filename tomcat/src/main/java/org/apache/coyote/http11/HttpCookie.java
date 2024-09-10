package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookiePairs = new HashMap<>();

    public HttpCookie(String rawCookie) {
        if (rawCookie == null) {
            return;
        }
        for (String rawCookiePair : rawCookie.split(";")) {
            String[] cookiePair = rawCookiePair.split("=");
            cookiePairs.put(cookiePair[0].trim(), cookiePair[1].trim());
        }
    }

    public String get(String key) {
        return cookiePairs.get(key);
    }
}

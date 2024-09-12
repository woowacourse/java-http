package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookie = new HashMap<>();

    public HttpCookie(String cookie) {
        if (cookie != null && !cookie.isEmpty()) {
            parse(cookie);
        }
    }

    public String get(String key) {
        return cookie.get(key);
    }

    private void parse(String cookie) {
        String[] cookies = cookie.split("; ");
        for (String pair : cookies) {
            String[] keyAndValue = pair.split("=");
            this.cookie.put(keyAndValue[0], keyAndValue[1]);
        }
    }
}

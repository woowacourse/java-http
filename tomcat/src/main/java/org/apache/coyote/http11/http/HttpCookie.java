package org.apache.coyote.http11.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> values = new HashMap<>();

    public HttpCookie() {
    }

    public HttpCookie(final String[] cookies) {
        init(cookies);
    }

    private void init(final String[] cookies) {
        for (String cookie : cookies) {
            String[] keyAndValue = cookie.split("=");
            values.put(keyAndValue[0], keyAndValue[1]);
        }
    }

    public boolean has(final String cookie) {
        return values.containsKey(cookie);
    }

    public String get(final String cookie) {
        return values.get(cookie);
    }
}

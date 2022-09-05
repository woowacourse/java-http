package org.apache.coyote.http11;

import java.util.Map;

public class Cookie {
    private final Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean hasCookie(String cookieName) {
        return cookies.containsKey(cookieName);
    }
}

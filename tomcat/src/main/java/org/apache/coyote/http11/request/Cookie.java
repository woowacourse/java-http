package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public class Cookie {

    private Map<String, String> cookies;

    public Cookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public Optional<String> getCookieOf(String cookieName) {
        if (!cookies.containsKey(cookieName)) {
            return Optional.empty();
        }
        return Optional.of(cookies.get(cookieName));
    }
}

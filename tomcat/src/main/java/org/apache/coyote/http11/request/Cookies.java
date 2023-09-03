package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Optional;

public class Cookies {

    private static final String SESSION_COOKIE = "JSESSIONID";
    private final Map<String, String> cookieValues;

    public Cookies(Map<String, String> cookieValues) {
        this.cookieValues = cookieValues;
    }

    public Optional<String> getCookieOf(String cookieName) {
        if (!cookieValues.containsKey(cookieName)) {
            return Optional.empty();
        }
        return Optional.of(cookieValues.get(cookieName));
    }

    public Optional<String> getSessionCookie() {
        if (!cookieValues.containsKey(SESSION_COOKIE)) {
            return Optional.empty();
        }
        return Optional.of(cookieValues.get(SESSION_COOKIE));
    }
}

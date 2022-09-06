package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Cookies {

    private static final String SESSION_KEY = "JSESSIONID";

    private final Map<String, String> cookies;

    public Cookies() {
        cookies = new HashMap<>();
    }

    public Cookies(final Map<String, String> cookies) {
        this();
        this.cookies.putAll(cookies);
    }

    public Optional<String> getValue(final String cookieKey) {
        return Optional.ofNullable(cookies.get(cookieKey));
    }

    public void addCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public void addSession(final String jSessionId) {
        cookies.put(SESSION_KEY, jSessionId);
    }

    public Optional<String> getSessionId() {
        return Optional.ofNullable(cookies.get(SESSION_KEY));
    }
}

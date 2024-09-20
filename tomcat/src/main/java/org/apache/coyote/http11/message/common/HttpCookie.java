package org.apache.coyote.http11.message.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public String get(String name) {
        return cookies.get(name);
    }

    public boolean hasSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public Optional<String> findSessionId() {
        if (cookies.containsKey(JSESSIONID)) {
            return Optional.of(cookies.get(JSESSIONID));
        }
        return Optional.empty();
    }
}

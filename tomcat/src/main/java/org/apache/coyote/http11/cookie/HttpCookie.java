package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.session.Session;

public final class HttpCookie {

    public static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies = new HashMap<>();

    public void addCookie(final String key, final String value) {
        cookies.put(key, value);
    }

    public void addSession(final Session session) {
        cookies.put(JSESSIONID, session.getId());
    }

    public boolean hasSession() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID);
    }

    public Optional<String> getSession() {
        return Optional.ofNullable(cookies.get(JSESSIONID));
    }
}

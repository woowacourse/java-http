package org.apache.catalina.vo;

import static org.apache.catalina.vo.Cookie.SESSION_COOKIE_ID;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cookies {

    private final Map<String, Cookie> cookies;

    public Cookies() {
        this.cookies = new HashMap<>();
    }

    public Cookies(String cookieString) {
        final Map<String, Cookie> result = new HashMap<>();
        final var split = cookieString.split(";");
        for (String pairString : split) {
            final var pairs = Arrays.stream(pairString.split("=", 2)).toList();
            if (pairs.isEmpty() || pairs.size() == 1 && pairString.indexOf("=") == pairString.length() - 1) {
                continue;
            }
            if (pairString.startsWith("=")) {
                throw new IllegalArgumentException();
            }

            final var key = pairs.getFirst().trim();
            final var value = pairs.getLast().trim();
            result.put(key, new Cookie(key, value));
        }
        this.cookies = result;
    }

    public Session extractSession() {
        final var sessionManager = SessionManager.getInstance();
        if (containsKey(SESSION_COOKIE_ID)) {
            final var sessionId = cookies.get(SESSION_COOKIE_ID).getValue();
            return sessionManager.findSession(sessionId);
        }
        return null;
    }

    public void add(final Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
    }

    public boolean containsKey(final String key) {
        return cookies.containsKey(key);
    }

    public Cookie get(final String key) {
        return cookies.get(key);
    }

    public List<Cookie> getAll() {
        return cookies.values().stream().toList();
    }
}

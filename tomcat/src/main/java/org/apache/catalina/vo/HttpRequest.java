package org.apache.catalina.vo;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Map;

public record HttpRequest(
    String method,
    String uri,
    Map<String, String> headers,
    String body
) {

    private static final String SESSION_COOKIE_ID = "JSESSIONID";

    public Session getSession(final boolean create) {
        final var sessionManager = SessionManager.getInstance();
        final var cookie = getCookie();
        if (cookie.containsKey(SESSION_COOKIE_ID)) {
            final var sessionId = cookie.get(SESSION_COOKIE_ID);
            final var session = sessionManager.findSession(sessionId);
            if (session == null) {
                return sessionManager.generateNewSession();
            }
            return session;
        }
        if (create) {
            return sessionManager.generateNewSession();
        }
        return null;
    }

    private HttpCookie getCookie() {
        if (headers.containsKey("Cookie")) {
            return new HttpCookie(headers.get("Cookie"));
        }
        return new HttpCookie();
    }
}

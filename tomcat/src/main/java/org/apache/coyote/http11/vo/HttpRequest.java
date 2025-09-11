package org.apache.coyote.http11.vo;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.util.Map;
import java.util.UUID;

public record HttpRequest(
    String method,
    String uri,
    Map<String, String> headers,
    String body
) {

    private static final String SESSION_COOKIE_ID = "JSESSIONID";

    public Session getSession() {
        final var sessionManager = SessionManager.getInstance();
        final var cookie = getCookie();
        if (cookie.containsKey(SESSION_COOKIE_ID)) {
            final var sessionId = cookie.get(SESSION_COOKIE_ID);
            return sessionManager.findSession(sessionId);
        }
        final var uuid = UUID.randomUUID();
        final var session = new Session(uuid.toString());
        sessionManager.add(session);
        return session;
    }

    public Session getSession(final boolean create) {
        final var sessionManager = SessionManager.getInstance();
        final var cookie = getCookie();
        if (cookie.containsKey(SESSION_COOKIE_ID)) {
            final var sessionId = cookie.get(SESSION_COOKIE_ID);
            final var session = sessionManager.findSession(sessionId);
            if (session == null) {
                final var uuid = UUID.randomUUID();
                final var newSession = new Session(uuid.toString());
                sessionManager.add(newSession);
                return newSession;
            }
            return session;
        }
        if (create) {
            return getSession();
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

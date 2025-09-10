package org.apache.coyote.http11.vo;

import org.apache.catalina.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public record HttpRequest(
    String method,
    String uri,
    Map<String, String> headers,
    String body
) {

    private static final String SESSION_COOKIE_ID = "JSESSIONID";
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    public Session getSession() {
        final var cookie = getCookie();
        if (cookie.containsKey(SESSION_COOKIE_ID)) {
            final var sessionId = cookie.get(SESSION_COOKIE_ID);
            return SESSION_MANAGER.findSession(sessionId);
        }
        final var uuid = UUID.randomUUID();
        final var session = new Session(uuid.toString());
        SESSION_MANAGER.add(session);
        return session;
    }

    public Session getSession(final boolean create) {
        final var cookie = getCookie();
        if (cookie.containsKey(SESSION_COOKIE_ID)) {
            final var sessionId = cookie.get(SESSION_COOKIE_ID);
            return SESSION_MANAGER.findSession(sessionId);
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

    private static class SessionManager {

        // static!
        private static final Map<String, Session> SESSIONS;

        static {
            SESSIONS = new HashMap<>();
        }

        public void add(final Session session) {
            SESSIONS.put(session.getId(), session);
        }

        public Session findSession(final String id) {
            return SESSIONS.get(id);
        }

        public void remove(final Session session) {
            SESSIONS.remove(session.getId());
        }
        private SessionManager() {}
    }
}

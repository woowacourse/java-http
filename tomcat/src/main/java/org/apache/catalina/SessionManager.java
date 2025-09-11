package org.apache.catalina;

import org.apache.catalina.vo.Cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private final Map<String, Session> sessions = new HashMap<>();
    private static SessionManager INSTANCE;

    public static SessionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SessionManager();
        }
        return INSTANCE;
    }

    public Session generateNewSession() {
        final var session = new Session();
        sessions.put(session.getId(), session);
        return session;
    }

    public Cookie generateSessionCookie(final Session session) {
        return Cookie.of(session);
    }

    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return sessions.get(id);
    }

    public void remove(final Session session) {
        sessions.remove(session.getId());
    }

    private SessionManager() {}
}

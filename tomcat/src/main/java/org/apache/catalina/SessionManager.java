package org.apache.catalina;

import org.apache.catalina.vo.Cookie;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
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

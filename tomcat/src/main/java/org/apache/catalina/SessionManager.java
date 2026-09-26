package org.apache.catalina;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public void add(Session session) {
        if (SESSIONS.putIfAbsent(session.getId(), session) != null) {
            throw new IllegalStateException("Duplicate session ID");
        }
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }
}

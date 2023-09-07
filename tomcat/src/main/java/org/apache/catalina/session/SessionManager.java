package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private static final class SessionManagerHolder {

        private static final SessionManager INSTANCE = new SessionManager();
    }

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return SessionManagerHolder.INSTANCE;
    }

    public Session createSession() {
        final var session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }
}

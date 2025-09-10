package org.apache.catalina;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public Session createSession() {
        final String id = UUID.randomUUID().toString();
        final Session session = new Session(id);
        add(session);
        return session;
    }

    public Session getOrCreateSession(final String sessionId, final boolean create) {
        if (sessionId != null) {
            final Session session = findSession(sessionId);
            if (session != null) {
                return session;
            }
        }
        
        if (create) {
            return createSession();
        }
        
        return null;
    }
}

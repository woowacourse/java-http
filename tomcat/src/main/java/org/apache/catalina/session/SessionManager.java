package org.apache.catalina.session;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions;

    private SessionManager() {
        sessions = new ConcurrentHashMap<>();
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws IOException {
        return sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }

    public boolean hasSession(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        return sessions.containsKey(sessionId);
    }
}

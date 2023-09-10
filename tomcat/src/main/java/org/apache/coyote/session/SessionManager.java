package org.apache.coyote.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        if (sessions.containsKey(id)) {
            return sessions.get(id);
        }
        throw new SessionException();
    }

    @Override
    public void remove(Session session) {
        session.invalidate();
        sessions.remove(session.getId());
    }
}

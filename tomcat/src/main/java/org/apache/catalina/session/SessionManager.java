package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        if (session == null) {
            throw new IllegalArgumentException("session must not be null");
        }
        if (session.getId() == null) {
            throw new IllegalArgumentException("session id must not be null");
        }
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("session id must not be null");
        }
        return SESSIONS.get(id);    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}

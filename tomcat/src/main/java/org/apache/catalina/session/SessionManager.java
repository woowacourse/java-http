package org.apache.catalina.session;

import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

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
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();

    private final Map<String, Session> sessions;

    private SessionManager() {
        this.sessions = new ConcurrentHashMap<>();
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(final Session session) {
        sessions.remove(session.getId());
    }
}

package org.apache.catalina.session;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static SessionManager getINSTANCE() {
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
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }
}

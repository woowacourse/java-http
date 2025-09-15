package org.apache.catalina;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

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

    private SessionManager() {
    }

    private static class SingletonHelper {

        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return SingletonHelper.INSTANCE;
    }
}

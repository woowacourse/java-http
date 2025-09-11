package org.apache.catalina.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public final class SessionManager implements Manager {

    private final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    private static class SingletonHolder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

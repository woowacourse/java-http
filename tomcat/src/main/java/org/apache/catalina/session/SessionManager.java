package org.apache.catalina.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {

    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public void remove(final Session session) {
        sessions.remove(session.getId());
    }

    public void clearAll() {
        sessions.clear();
    }
}

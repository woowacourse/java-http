package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Session createSession() {
        Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public Session renewSession(final Session session) {
        remove(session.getId());
        return createSession();
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
    public void remove(final String id) {
        sessions.remove(id);
    }
}

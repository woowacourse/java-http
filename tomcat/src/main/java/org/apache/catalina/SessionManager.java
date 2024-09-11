package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private static final Map<String, Session> STORE = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        STORE.put(session.getId(), session);
        return session;
    }

    @Override
    public void add(Session session) {
        STORE.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(String id) {
        return Optional.ofNullable(STORE.get(id));
    }

    @Override
    public void remove(Session session) {
        STORE.remove(session.getId());
    }

    public void clear() {
        STORE.clear();
    }

    public Map<String, Session> getStore() {
        return STORE;
    }
}

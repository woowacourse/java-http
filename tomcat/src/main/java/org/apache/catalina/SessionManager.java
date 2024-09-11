package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();

    private static final Map<String, Session> store = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public Session createSession() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        store.put(session.getId(), session);
        return session;
    }

    @Override
    public void add(Session session) {
        store.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public void remove(Session session) {
        store.remove(session.getId());
    }

    public void clear() {
        store.clear();
    }

    public Map<String, Session> getStore() {
        return store;
    }
}

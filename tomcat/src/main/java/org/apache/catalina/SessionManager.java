package org.apache.catalina;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private static final Map<String, Session> STORE = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        STORE.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(String id) throws IOException {
        return Optional.ofNullable(STORE.get(id));
    }

    @Override
    public void remove(Session session) {
        STORE.remove(session.getId());
    }
}

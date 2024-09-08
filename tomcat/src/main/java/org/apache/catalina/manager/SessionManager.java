package org.apache.catalina.manager;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> STORE = new ConcurrentHashMap<>();

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

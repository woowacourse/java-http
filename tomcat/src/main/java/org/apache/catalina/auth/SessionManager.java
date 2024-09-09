package org.apache.catalina.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager instance = new SessionManager();

    private SessionManager() {}

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return Optional.of(SESSIONS.get(id));
        }
        return Optional.empty();
    }

    @Override
    public void remove(Session session) {
        SESSIONS.keySet().stream()
                .filter(key -> SESSIONS.get(key).equals(session))
                .findAny()
                .ifPresent(SESSIONS::remove);
    }
}

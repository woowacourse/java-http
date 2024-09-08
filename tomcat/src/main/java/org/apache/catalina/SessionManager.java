package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private static SessionManager instance;

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    private SessionManager() {
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

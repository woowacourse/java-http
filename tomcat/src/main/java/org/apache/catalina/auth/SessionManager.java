package org.apache.catalina.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(Session session) {
        String id = UUID.randomUUID().toString();
        SESSIONS.put(id, session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.keySet().stream()
                .filter(key -> SESSIONS.get(key).equals(session))
                .findAny()
                .ifPresent(SESSIONS::remove);
    }

    private SessionManager() {}
}

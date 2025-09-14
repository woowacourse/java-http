package org.apache.catalina;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static SessionManager instance;
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    @Override
    public void add(Session session) {
        if (session != null && session.getId() != null) {
            SESSIONS.put(session.getId(), session);
        }
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        if (session != null && session.getId() != null) {
            SESSIONS.remove(session.getId());
        }
    }

    public String generateJSESSIONID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}


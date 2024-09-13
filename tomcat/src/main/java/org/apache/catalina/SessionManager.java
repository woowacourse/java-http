package org.apache.catalina;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        if (!SESSIONS.containsKey(id)) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.entrySet()
                .removeIf(entry -> session.equals(entry.getValue()));
    }
}

package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session find(final String id) {
        return SESSIONS.get(id);
    }

    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public Session create() {
        final String id = UUID.randomUUID().toString();
        final Session session = new Session(id);
        add(session);
        return session;
    }

    public void invalidate(String id) {
        Session session = find(id);
        if (session != null) {
            session.invalidate();
            remove(id);
        }
    }
}

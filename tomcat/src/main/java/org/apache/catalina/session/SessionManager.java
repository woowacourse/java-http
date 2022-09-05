package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        if (!SESSIONS.containsKey(id)) {
            throw new IllegalArgumentException("No Such Session exists");
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        if (!SESSIONS.containsKey(session.getId())) {
            throw new IllegalArgumentException("No Such Session exists");
        }
        SESSIONS.remove(session.getId());
    }
}

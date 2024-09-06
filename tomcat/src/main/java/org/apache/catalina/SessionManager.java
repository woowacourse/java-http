package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.Session;

public class SessionManager implements Manager {
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {}

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}


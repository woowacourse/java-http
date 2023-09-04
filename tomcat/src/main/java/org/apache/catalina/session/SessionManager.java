package org.apache.catalina.session;

import org.apache.catalina.Manager;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        return null;
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

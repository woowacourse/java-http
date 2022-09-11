package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {
    // static!
    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        if (!SESSIONS.containsKey(session.getId())) {
            SESSIONS.put(session.getId(), session);
        }
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public boolean existSession(final String id) {
        return SESSIONS.containsKey(id);
    }
}

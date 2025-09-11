package org.apache.coyote.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.putIfAbsent(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public boolean hasSession(final String id) {
        return SESSIONS.containsKey(id);
    }

    public Map<String, Session> getSessions() {
        return Collections.unmodifiableMap(SESSIONS);
    }
}

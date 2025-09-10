package org.apache.coyote.session;

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
        return SESSIONS;
    }
}

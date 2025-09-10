package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> sessions = new HashMap<>();

    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return sessions.get(id);
    }

    public void remove(final String id) {
        sessions.remove(id);
    }
}

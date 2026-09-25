package org.apache.coyote.http11;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> sessions = new HashMap<>();

    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    public Session findSession(String id) throws IOException {
        return sessions.get(id);
    }

    public void remove(Session session) {
        sessions.remove(session.getId());
    }
}

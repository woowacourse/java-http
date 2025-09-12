package org.apache.coyote.http11;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager {

    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

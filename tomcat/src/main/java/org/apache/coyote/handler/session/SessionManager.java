package org.apache.coyote.handler.session;

import org.apache.catalina.Manager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws IOException {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        return null;
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

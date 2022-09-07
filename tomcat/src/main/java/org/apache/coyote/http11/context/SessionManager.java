package org.apache.coyote.http11.context;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(Session session) {
        if (!SESSIONS.containsKey(session.getId())) {
            SESSIONS.put(session.getId(), session);
        }
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        throw new UnsupportedOperationException();
    }
}

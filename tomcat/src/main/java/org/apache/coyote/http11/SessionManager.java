package org.apache.coyote.http11;

import org.apache.catalina.Manager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public SessionManager() {
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), new Session(session.getId()));
    }

    @Override
    public Session findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

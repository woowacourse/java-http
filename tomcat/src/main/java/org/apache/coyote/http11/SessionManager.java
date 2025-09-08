package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    private SessionManager() {}

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        Session session = SESSIONS.get(id);
        return session;
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

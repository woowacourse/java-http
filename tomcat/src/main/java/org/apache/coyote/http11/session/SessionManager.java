package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {
    // static!
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static SessionManager instance;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}

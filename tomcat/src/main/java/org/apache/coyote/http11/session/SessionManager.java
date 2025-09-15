package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

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
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public void changeSessionId(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("session should not be null");
        }
        SESSIONS.remove(session.getId());
        session.changeId();
        SESSIONS.put(session.getId(), session);
    }
}

package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager sessionManager = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return sessionManager;
    }

    public boolean hasSession(final String jSessionId) {
        return SESSIONS.containsKey(jSessionId);
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
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

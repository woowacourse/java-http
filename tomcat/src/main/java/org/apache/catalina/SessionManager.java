package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, HttpSession> sessions = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        sessions.remove(session.getId());
    }
}

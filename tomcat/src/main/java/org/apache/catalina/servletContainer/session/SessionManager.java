package org.apache.catalina.servletContainer.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(final Session session) {

    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {

    }
}

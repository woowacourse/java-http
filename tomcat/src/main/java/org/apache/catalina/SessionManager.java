package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final SessionManager sessionManager = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
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

    public static SessionManager getSessionManager() {
        return sessionManager;
    }

    public boolean hasSession(String jsessionid) {
        return SESSIONS.containsKey(jsessionid);
    }
}



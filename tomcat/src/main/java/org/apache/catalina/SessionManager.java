package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(String id) {

    }

    @Override
    public void add(HttpSession session) {

    }

    @Override
    public void remove(HttpSession session) {

    }
}

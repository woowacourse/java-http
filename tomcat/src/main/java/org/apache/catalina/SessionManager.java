package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();

    private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    @Override
    public void add(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        sessions.remove(session.getId());
    }
}

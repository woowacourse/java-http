package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public SessionManager() {}

    @Override
    public Session findSession(String id) {
        return id == null ? null : sessions.get(id);
    }

    @Override
    public void add(HttpSession session) {
        Session existing = sessions.putIfAbsent(session.getId(), (Session) session);
        if (existing != null) {
            throw new IllegalStateException("Session ID already exists: " + session.getId());
        }
    }

    @Override
    public void remove(HttpSession session) {
        sessions.remove(session.getId(), (Session) session);
    }

}

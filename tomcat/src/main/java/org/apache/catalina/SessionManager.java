package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Session findSession(String id) {
        return id == null ? null : sessions.get(id);
    }

    @Override
    public void add(HttpSession session) {
        sessions.put(session.getId(), (Session) session);
    }

    @Override
    public void remove(HttpSession session) {
        sessions.remove(session.getId(), session);
    }

    public SessionManager() {}
}

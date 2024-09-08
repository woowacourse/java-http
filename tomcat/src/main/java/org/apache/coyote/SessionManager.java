package org.apache.coyote;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private final Map<String, HttpSession> sessions;

    public SessionManager() {
        sessions = new ConcurrentHashMap<>();
    }

    @Override
    public void add(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        sessions.remove(session.getId());
    }
}

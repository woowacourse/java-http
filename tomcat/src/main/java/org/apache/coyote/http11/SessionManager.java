package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();

    private final Map<String, HttpSession> sessions = new HashMap<>();

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
        return sessions.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        sessions.remove(session.getId());
    }
}

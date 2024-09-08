package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        HttpSession session = SESSIONS.get(id);

        if (session == null || session.getAttribute("user") == null) {
            return null;
        }

        return session;
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}
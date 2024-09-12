package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {
    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();

    private static SessionManager instance = null;

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }
}

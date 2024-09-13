package com.techcourse.session;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public boolean contains(String sessionId) {
        return SESSIONS.containsKey(sessionId);
    }

    public boolean isValidJSessionId(String jSessionId) {
        return SESSIONS.values().stream().anyMatch(session -> session.getAttribute("JSESSIONID").equals(jSessionId));
    }

    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}

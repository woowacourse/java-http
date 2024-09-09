package com.techcourse.session;

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
}

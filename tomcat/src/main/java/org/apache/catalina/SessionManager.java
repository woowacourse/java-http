package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

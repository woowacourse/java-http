package org.apache.coyote.util;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.Session;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), (Session) session);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    public Session findSession(String id) {
        return SESSIONS.get(id);
    }
}

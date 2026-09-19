package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

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

    public static SessionManager getInstance() {
        return INSTANCE;
    }
}

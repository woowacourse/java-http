package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    public SessionManager() {
    }

    public static SessionManager getInstance() {
        return SessionManagerHolder.INSTANCE;
    }

    private static class SessionManagerHolder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), (Session) session);
    }

    @Override
    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    public List<String> getSessions() {
        return List.copyOf(SESSIONS.keySet());
    }

    public Optional<HttpSession> getSession(String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }
}

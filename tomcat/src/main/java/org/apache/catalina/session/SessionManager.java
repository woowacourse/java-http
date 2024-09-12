package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private SessionManager() {
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

    public Map<String, Session> getStore() {
        return SESSIONS;
    }

    public Optional<Session> getSession(String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public void clear() {
        SESSIONS.clear();
    }
}

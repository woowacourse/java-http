package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(final HttpSession session) {
        if (session instanceof Session s) {
            sessions.put(s.getId(), s);
        }
    }

    @Override
    public HttpSession findSession(final String id) throws IOException {
        return (HttpSession) sessions.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        sessions.remove(session.getId());
    }

    public Session createSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public Session getSession(String sessionId, boolean create) {

        if (sessionId != null) {
            Session session = sessions.get(sessionId);
            if (session != null) {
                return session;
            }
        }

        if (create) {
            return createSession();
        }

        return null;
    }

    private SessionManager() {
    }

    private static class SingletonHelper {

        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return SingletonHelper.INSTANCE;
    }
}

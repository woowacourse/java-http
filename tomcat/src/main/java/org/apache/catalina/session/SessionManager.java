package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.HttpCookie;

public class SessionManager implements Manager {

    private final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    private SessionManager() {
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public Session createSession() {
        String jsessionId = HttpCookie.newSessionId();
        Session session = new Session(jsessionId);
        add(session);
        return session;
    }
}

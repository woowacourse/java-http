package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.cookie.HttpCookie;

public class SessionManager {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    
    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createAndSaveSession(final Map<String, Object> values) {
        final String sessionId = UUID.randomUUID().toString();
        final Session session = new Session(sessionId, values);
        add(session);
        return session;
    }

    public HttpCookie createSessionCookie(Session session) {
        return HttpCookie.of(SESSION_COOKIE_NAME, session.getId());
    }

    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    private SessionManager() {
    }
}

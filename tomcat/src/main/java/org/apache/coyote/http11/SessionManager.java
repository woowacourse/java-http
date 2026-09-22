package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager INSTANCE = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public void add(final HttpSession session) {
        if (!(session instanceof Session)) {
            throw new IllegalArgumentException("SessionManager에는 Session만 등록할 수 있습니다.");
        }
        final Session customSession = (Session) session;
        SESSIONS.put(customSession.getId(), customSession);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    public void remove(final String id) {
        if (id == null) {
            return;
        }
        SESSIONS.remove(id);
    }

    @Override
    public void remove(final HttpSession session) {
        if (session == null) {
            return;
        }
        remove(session.getId());
    }
}

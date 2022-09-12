package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.exception.unauthorized.InvalidSessionException;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        if (session.isLoggedIn()) {
            SESSIONS.put(session.getId(), session);
        }
    }

    @Override
    public Session findSession(final String id) {
        if (id.isBlank() || !SESSIONS.containsKey(id)) {
            throw new InvalidSessionException();
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

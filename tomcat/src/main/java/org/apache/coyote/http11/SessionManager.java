package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new LinkedHashMap<>();

    private static class SessionManagerInstanceHolder {
        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return SessionManagerInstanceHolder.INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.id(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        if (!SESSIONS.containsKey(id)) {
            return Optional.empty();
        }
        final Session foundSession = SESSIONS.get(id);
        foundSession.found();
        return Optional.of(foundSession);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {}
}

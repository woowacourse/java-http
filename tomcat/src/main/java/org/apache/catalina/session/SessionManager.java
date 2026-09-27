package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        final Session registeredSession = SESSIONS.get(id);
        if (registeredSession == null)  {
            final Session newSession  = new Session(id);
            SESSIONS.put(id, newSession);
            return newSession;
        }
        return registeredSession;
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

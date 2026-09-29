package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private static class SessionManagerInstanceHolder {

        private static final SessionManager INSTANCE = new SessionManager();
    }

    public static SessionManager getInstance() {
        return SessionManagerInstanceHolder.INSTANCE;
    }

    public Session createNewSession() {
        final UUID uuid = UUID.randomUUID();
        final Session newSession = Session.init(uuid.toString());
        add(newSession);

        return newSession;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.id(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {
    }
}

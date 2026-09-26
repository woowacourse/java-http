package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public Session findOrCreate(String id) {
        Session session = SESSIONS.get(id);
        if (session == null) {
            return create();
        }
        session.access();
        return session;
    }

    private Session create() {
        Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(String id) {
        SESSIONS.remove(id);
    }
}

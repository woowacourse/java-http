package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new HashMap<>();

    @Override
    public Session createSession() {
        String id = UUID.randomUUID().toString();
        Session session = new Session(id);
        sessions.put(id, session);
        return session;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }

    public SessionManager() {}
}

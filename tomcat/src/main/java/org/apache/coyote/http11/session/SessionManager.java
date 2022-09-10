package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(String id) {
        Session session = SESSIONS.get(id);
        return Optional.ofNullable(session);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

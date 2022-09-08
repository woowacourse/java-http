package org.apache.coyote.common.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    // static!
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final String key, final Session session) {
        SESSIONS.put(key, session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        return SESSIONS.values()
                .stream()
                .filter(session -> session.getId().equals(id))
                .findFirst();
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

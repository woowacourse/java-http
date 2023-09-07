package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

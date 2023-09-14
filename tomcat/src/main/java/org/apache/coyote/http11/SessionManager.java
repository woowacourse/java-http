package org.apache.coyote.http11;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(final Session session) {
        sessions.remove(session.getId());
    }

    public boolean isExist(final String id) {
        return Objects.nonNull(id) && sessions.containsKey(id);
    }

}

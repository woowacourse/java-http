package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        for (final Entry<String, Session> sessionEntry : SESSIONS.entrySet()) {
            if (sessionEntry.getValue().equals(session)) {
                SESSIONS.remove(sessionEntry.getKey());
                break;
            }
        }
    }
}

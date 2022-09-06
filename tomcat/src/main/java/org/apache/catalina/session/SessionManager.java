package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.exception.unauthorised.LoginFailException;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id.isBlank() || !SESSIONS.containsKey(id)) {
            throw new LoginFailException();
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}

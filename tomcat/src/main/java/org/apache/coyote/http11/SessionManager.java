package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager(new HashMap<>());

    private final Map<String, Session> session;

    private SessionManager(final Map<String, Session> session) {
        this.session = session;
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        this.session.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return session.get(id);
    }

    @Override
    public void remove(final Session session) {
        this.session.remove(session.getId());
    }
}

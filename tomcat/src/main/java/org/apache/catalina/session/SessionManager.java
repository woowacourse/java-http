package org.apache.catalina.session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> Sessions = new HashMap<>();

    @Override
    public void add(Session session) {
        Sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return Sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        Sessions.remove(session.getId());
    }
}

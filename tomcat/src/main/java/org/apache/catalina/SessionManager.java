package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    @Override
    public void add(final Session session) {

    }

    @Override
    public Session findSession(final String id) {
        return null;
    }

    @Override
    public void remove(final String id) {
    }

    private SessionManager() {}
}

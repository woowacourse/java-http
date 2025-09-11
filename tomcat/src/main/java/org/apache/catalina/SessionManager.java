package org.apache.catalina;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.HttpSession;

public class SessionManager implements Manager {

    private static final SessionManager instance = new SessionManager();
    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    @Override
    public void add(final HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    public static SessionManager getInstance() {
        return instance;
    }
}

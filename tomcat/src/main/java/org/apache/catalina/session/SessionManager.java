package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        if (!SESSIONS.containsKey(id)) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}

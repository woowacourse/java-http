package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession session) {
        if (session != null) {
            SESSIONS.put(session.getId(), session);
        }
    }

    @Override
    public HttpSession findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        if (session != null) {
            SESSIONS.remove(session.getId());
        }
    }
}

package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public Session getOrCreate(final String id) {
        return SESSIONS.computeIfAbsent(id, Session::new);
    }

    @Override
    public void add(final HttpSession session) {
        if (!(session instanceof Session)) {
            throw new IllegalArgumentException("Only Session is supported");
        }
        SESSIONS.put(session.getId(), (Session) session);
    }

    @Override
    public HttpSession findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        if (session != null) {
            SESSIONS.remove(session.getId(), session);
        }
    }
}

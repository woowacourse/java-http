package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public Session getOrCreate(final String id) {
        return sessions.computeIfAbsent(id, Session::new);
    }

    @Override
    public void add(final HttpSession session) {
        if (!(session instanceof Session)) {
            throw new IllegalArgumentException("Only Session is supported");
        }
        sessions.put(session.getId(), (Session) session);
    }

    @Override
    public HttpSession findSession(final String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        if (session != null) {
            sessions.remove(session.getId(), session);
        }
    }
}

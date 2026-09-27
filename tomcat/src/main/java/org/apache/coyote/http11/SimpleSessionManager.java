package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.catalina.Manager;

public class SimpleSessionManager implements Manager {

    private final ConcurrentMap<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        if (id == null) {
            return null;
        }
        return sessions.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        if (session == null) {
            return;
        }

        sessions.remove(session.getId(), session);
    }
}

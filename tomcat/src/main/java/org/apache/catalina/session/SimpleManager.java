package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleManager implements Manager {

    private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void add(final HttpSession session) {
        Objects.requireNonNull(session, "session must not be null");
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) throws IOException {
        if (id == null) {
            return null;
        }

        return sessions.get(id);
    }

    @Override
    public void remove(final HttpSession session) {
        if (session != null) {
            sessions.remove(session.getId());
        }
    }
}

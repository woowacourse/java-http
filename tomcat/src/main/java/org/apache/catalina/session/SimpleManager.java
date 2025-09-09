package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleManager implements Manager {

    private static final SimpleManager INSTANCE = new SimpleManager();

    private final Map<String, HttpSession> sessions = new ConcurrentHashMap<>();

    private SimpleManager() {
    }

    public static SimpleManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(final HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(final String id) {
        if (id == null) {
            return null;
        }

        final var session = sessions.get(id);
        if (session instanceof SimpleHttpSession s) {
            s.setNew(false);
        }

        return session;
    }

    @Override
    public void remove(final HttpSession session) {
        if (session != null) {
            sessions.remove(session.getId());
        }
    }
}

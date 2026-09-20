package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    @Override
    public void add(HttpSession session) {
        HttpSession existingSession = SESSIONS.putIfAbsent(session.getId(), session);
        if (existingSession != null) {
            log.warn("Session with id {} already exists", session.getId());
            throw new IllegalStateException("Session with id " + session.getId() + " already exists");
        }
    }

    @Override
    public HttpSession findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId(), session);
    }

    @Override
    public void removeAll() {
        SESSIONS.clear();
    }
}

package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager implements Manager {

    private static final Logger LOG = LoggerFactory.getLogger(SessionManager.class);
    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager get() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
        LOG.info("\nSession: " + SESSIONS.keySet());
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
        System.out.println("SESSIONS = " + SESSIONS);
    }

    public int size() {
        return SESSIONS.size();
    }

    public Session searchByUser(User user) {
        return SESSIONS.values()
                .stream()
                .filter(session -> session.getAttribute("user").equals(user))
                .findFirst()
                .orElse(null);
    }
}

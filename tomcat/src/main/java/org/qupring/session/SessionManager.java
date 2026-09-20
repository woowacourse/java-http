package org.qupring.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final SessionManager INSTANCE =
            new SessionManager();

    private final Map<String, Session> SESSIONS =
            new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public Session findSession(String id) {
        if (id == null) {
            return null;
        }

        return SESSIONS.get(id);
    }

    public void remove(String id) {
        if (id != null) {
            SESSIONS.remove(id);
        }
    }

}

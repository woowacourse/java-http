package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.entrySet().stream()
                .filter(session -> session.getKey().equals(id))
                .findAny()
                .map(Entry::getValue).orElse(null);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

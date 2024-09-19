package org.apache.catalina.session;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager(new ConcurrentHashMap<>());

    private final Map<String, Session> sessions;

    private SessionManager(Map<String, Session> sessions) {
        this.sessions = sessions;
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    @Override
    public void add(String id, Session session) {
        sessions.put(id, session);
    }

    @Override
    public Optional<Session> findSession(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    @Override
    public void remove(String id) {
        sessions.remove(id);
    }

    public boolean hasId(String id) {
        return sessions.containsKey(id);
    }

    public String store(String attributeName, Object attributeValue) {
        Session session = new Session();
        session.setAttribute(attributeName, attributeValue);

        String id = UUID.randomUUID().toString();
        this.add(id, session);
        return id;
    }
}

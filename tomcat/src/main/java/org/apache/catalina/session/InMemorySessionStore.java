package org.apache.catalina.session;

import com.java.http.session.Session;
import com.java.http.session.SessionStore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionStore implements SessionStore {

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @Override
    public Session create() {
        Session session = new SimpleSession();
        sessions.put(session.id(), session);
        return session;
    }

    @Override
    public Session get(String sessionId) {
        return sessions.get(sessionId);
    }

    @Override
    public void changeSessionId(Session session) {
        sessions.remove(session.id());
        session.changeSessionId();
        sessions.put(session.id(), session);
    }
}

package org.apache.catalina.session;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public SessionManager() {
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    public Session getSession(final HttpRequest httpRequest) {
        String sessionId = httpRequest.extractSessionId();
        if (SESSIONS.containsKey(sessionId)) {
            return findSession(sessionId);
        }
        Session session = new Session(UUID.randomUUID().toString());
        add(session);
        return session;
    }
}

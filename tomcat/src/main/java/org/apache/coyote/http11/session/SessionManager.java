package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

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

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public Session getSession(HttpRequest httpRequest) {
        String sessionId = httpRequest.extractSessionId();
        if (SESSIONS.containsKey(sessionId)) {
            return findSession(sessionId);
        }
        Session session = new Session(sessionId);
        add(session);
        return session;
    }
}

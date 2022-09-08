package org.apache.coyote.http11;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final ConcurrentHashMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static Session getSession(final HttpRequest request) {
        Cookies cookies = request.getCookies();

        if (cookies.hasSessionId() && SESSIONS.containsKey(cookies.getSessionId())) {
            return SESSIONS.get(cookies.getSessionId());
        }

        return createSession();
    }

    private static Session createSession() {
        final Session session = new Session(UUID.randomUUID().toString());
        SESSIONS.put(session.getId(), session);
        return session;
    }
}

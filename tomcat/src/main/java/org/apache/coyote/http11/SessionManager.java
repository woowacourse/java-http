package org.apache.coyote.http11;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Session add(final String jsessionid) {
        final Session session = new Session(jsessionid);
        SESSIONS.put(jsessionid, session);
        return session;
    }

    public static Optional<Session> findSession(final String jsessionid) {
        return Optional.ofNullable(SESSIONS.get(jsessionid));
    }
}

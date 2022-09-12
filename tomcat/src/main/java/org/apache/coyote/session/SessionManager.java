package org.apache.coyote.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionManager {

    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {}

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
        log.info("[Session Manger] Session Create - Session ID : {}", session.getId());
    }

    public static Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }
}

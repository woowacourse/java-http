package org.apache.catalina;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.exception.InvalidSessionException;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Session;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static SessionManager getInstance() {
        return SessionManagerHolder.INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public void validateSession(final HttpCookie httpCookie) {
        final String sessionID = httpCookie.getJSessionID();
        if (!SESSIONS.containsKey(sessionID)) {
            throw new InvalidSessionException();
        }
    }

    private static final class SessionManagerHolder {

        private static final SessionManager INSTANCE = new SessionManager();
    }
}

package org.apache.coyote.http11.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }

    public Session getSessionId(final String sessionId) {
        return Optional.ofNullable(sessionId)
                .map(id -> SESSIONS.computeIfAbsent(id, this::getSession))
                .orElseGet(this::makeNewSession);
    }

    private Session getSession(final String id) {
        final Session session = SESSIONS.get(id);
        if (session == null) {
            return makeNewSession();
        }
        if (session.isExpired()) {
            remove(session);
            return makeNewSession();
        }
        return session;
    }

    private Session makeNewSession() {
        final Session session = new Session();
        add(session);
        return session;
    }

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        public static final SessionManager instance = new SessionManager();
    }
}

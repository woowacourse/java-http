package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager sessionManager = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Optional<Session> findSession(final String id) {
        return Optional.ofNullable(SESSIONS.get(id));
    }

    public boolean containsSession(final Session session){
        return SESSIONS.containsValue(session);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }
}

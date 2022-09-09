package nextstep.jwp.http;

import org.apache.catalina.Manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final SessionManager sessionManager = new SessionManager();
    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static SessionManager get() {
        return sessionManager;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        if (session != null) {
            SESSIONS.remove(session.getId());
        }
    }

    public int size() {
        return SESSIONS.size();
    }
}

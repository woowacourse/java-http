package nextstep.jwp.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Manager sessionManager = new SessionManager();
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    private SessionManager() {
    }

    public static Manager getInstance() {
        return sessionManager;
    }

    @Override
    public void add(Session session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return sessions.get(id);
    }

    @Override
    public void remove(Session session) {
        sessions.remove(session.getId());
    }

}

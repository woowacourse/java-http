package nextstep.jwp.security;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Manager sessionManager = new SessionManager();
    private static final Map<String, Session> sessions = new HashMap<>();

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

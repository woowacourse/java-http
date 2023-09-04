package nextstep.jwp.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager  {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static Session createSession(String key) {
        Session session = new Session(key);
        SESSIONS.put(key, session);
        return session;
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(String id)  {
        return SESSIONS.get(id);
    }
}

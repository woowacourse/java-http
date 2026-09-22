package org.apache.catalina;

import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final SessionManager INSTANCE = new SessionManager();

    private final Map<String, Session> sessionInfo = new HashMap<>();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public void add(Session session) {
        sessionInfo.put(session.getId(), session);
    }

    public Session findSession(final String id) {
        return sessionInfo.get(id);
    }

    public void clear() {
        sessionInfo.clear();
    }

    public boolean hasUser(String sessionId) {
        Session session = findSession(sessionId);
        if (session == null) {
            return false;
        }

        return getUser(session) != null;
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }
}

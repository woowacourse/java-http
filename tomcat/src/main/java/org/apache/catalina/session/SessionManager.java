package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nextstep.jwp.model.user.User;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();
    private static final SessionManager sessionManager = new SessionManager();
    private static final String USER_KEY = "user";

    private SessionManager() {}

    public static SessionManager connect() {
        return sessionManager;
    }

    public String createSession() {
        Session session = new Session();
        SESSIONS.put(session.getId(), session);
        return session.getId();
    }

    public void addUserInSession(String sessionId, User user) {
        Session session = SESSIONS.get(sessionId);
        session.setAttribute(USER_KEY, user);
    }

    public boolean checkLogin(String sessionId) {
        Session session = SESSIONS.get(sessionId);
        return session.hasAttribute(USER_KEY);
    }
}

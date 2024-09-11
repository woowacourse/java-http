package org.apache.coyote.session;

import com.techcourse.model.User;

public class SessionService {

    private static final SessionService INSTANCE = new SessionService();

    private final SessionManager sessionManager = SessionManager.getInstance();

    private SessionService() {
    }

    public static SessionService getInstance() {
        return INSTANCE;
    }

    public void registerSession(String sessionId, User user) {
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    public boolean isSessionExist(String sessionId) {
        return sessionManager.isExistSession(sessionId);
    }
}

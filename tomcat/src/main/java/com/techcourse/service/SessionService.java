package com.techcourse.service;

import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class SessionService {

    private static final SessionService INSTANCE = new SessionService();

    private final SessionManager sessionManager = SessionManager.getInstance();

    private SessionService() {
    }

    public void registerSession(String sessionId, User user) {
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        sessionManager.add(session);
    }

    public boolean isSessionExist(String sessionId) {
        return sessionManager.isExistSession(sessionId);
    }

    public static SessionService getInstance() {
        return INSTANCE;
    }
}

package com.techcourse.service;

import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class SessionService {

    private final SessionManager sessionManager;

    public SessionService() {
        this.sessionManager = new SessionManager();
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

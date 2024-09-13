package com.techcourse.service;

import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;

import com.techcourse.model.User;


public class AuthService {

    public boolean isLogin(String sessionId) {
        Optional<Session> session = SessionManager.getInstance().findSession(sessionId);
        return session.isPresent();
    }

    public Session createSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(session.getId(), user);
        SessionManager.getInstance().add(session);
        return session;
    }
}

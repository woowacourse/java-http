package com.techcourse.service;

import java.util.Optional;
import java.util.UUID;

import org.apache.catalina.auth.HttpCookie;
import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;


public class AuthService {

    public boolean isLogin(HttpCookie cookie) {
        return SessionManager.getInstance()
                .findSession(cookie.getAuthSessionId())
                .isPresent();
    }

    public Optional<User> authenticateUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return user;
        }
        return Optional.empty();
    }

    public Session createSession(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(session.getId(), user);
        SessionManager.getInstance().add(session);
        return session;
    }
}

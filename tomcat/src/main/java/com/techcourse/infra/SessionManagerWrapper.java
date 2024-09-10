package com.techcourse.infra;

import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.http11.Http11Cookie;

public class SessionManagerWrapper {

    private final Manager manager;

    public SessionManagerWrapper(Manager manager) {
        this.manager = manager;
    }

    public void add(Session session) {
        manager.add(session);
    }

    public Optional<Session> findById(String sessionId) {
        return Optional.ofNullable(manager.findSession(sessionId));
    }

    public Optional<Session> findBySessionCookie(Http11Cookie sessionCookie) {
        if (sessionCookie.isSessionCookie()) {
            return findById(sessionCookie.value());
        }
        return Optional.empty();
    }

    public void remove(Session session) {
        manager.remove(session);
    }
}

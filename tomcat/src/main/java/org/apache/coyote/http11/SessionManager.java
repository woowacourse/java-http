package org.apache.coyote.http11;

import nextstep.jwp.model.User;
import org.apache.catalina.Manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public Session add(User user) {
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        this.add(session);
        return session;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }
}

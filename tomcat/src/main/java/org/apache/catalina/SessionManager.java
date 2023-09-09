package org.apache.catalina;

import nextstep.jwp.model.User;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager implements Manager {

    private static final SessionManager INSTANCE = new SessionManager();
    private static final ConcurrentMap<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public static SessionManager InstanceOf() {
        return INSTANCE;
    }

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public void addLoginSession(final String jSessionId, final User user) {
        Session session = new Session(jSessionId);
        session.setAttribute("user", user);
        INSTANCE.add(session); //세션 매니저에 세션을 추가한다.
    }

    @Override
    public Session findSession(final String id) {
        if (id == null) {
            return null;
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId(), session);
    }


    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    private SessionManager() {
    }

}

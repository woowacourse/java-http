package org.apache.coyote.handler;

import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.http.message.HttpSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SessionManager implements Manager {

    private static final ConcurrentMap<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();
    private static final String USER_KEY = "user";

    @Override
    public void add(final HttpSession httpSession) {
        SESSIONS.put(httpSession.getId(), httpSession);
    }

    @Override
    public HttpSession findSession(final String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final String id) {
        SESSIONS.remove(id);
    }

    public HttpSession createSession(final User user) {
        final HttpSession session = HttpSession.of(USER_KEY, user);
        add(session);
        return session;
    }
}

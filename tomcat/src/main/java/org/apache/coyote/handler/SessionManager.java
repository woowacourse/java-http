package org.apache.coyote.handler;

import nextstep.jwp.model.User;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.http.message.HttpSession;

import java.util.HashMap;
import java.util.Map;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();
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

package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.model.user.User;
import org.apache.catalina.Manager;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();
    private static final SessionManager sessionManager = new SessionManager();
    private static final String USER_KEY = "user";

    private SessionManager() {}

    public static SessionManager of() {
        return sessionManager;
    }

    @Override
    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(String id) throws IOException {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(Session session) {
        SESSIONS.remove(session.getId());
    }

    public Session generateSession(User user) {
        Session session = new Session();
        session.setAttribute(USER_KEY, user);
        return session;
    }
}

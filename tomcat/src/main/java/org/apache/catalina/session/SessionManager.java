package org.apache.catalina.session;

import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();
    private static final SessionManager SESSION_MANAGER = new SessionManager();
    private static final String ATTRIBUTE_USER_NAME = "user";

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        HttpSession session = SESSIONS.get(id);

        if (session == null || session.getAttribute(ATTRIBUTE_USER_NAME) == null) {
            return null;
        }

        return session;
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }

    public HttpSession createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        JSession jSession = new JSession(sessionId);
        jSession.setAttribute(ATTRIBUTE_USER_NAME, user);
        add(jSession);

        return jSession;
    }

    public HttpSession getSession(HttpRequest request) {
        String sessionId = request.cookies().get(JSession.COOKIE_NAME);
        if (sessionId == null) {
            return null;
        }

        return SESSIONS.get(sessionId);
    }
}

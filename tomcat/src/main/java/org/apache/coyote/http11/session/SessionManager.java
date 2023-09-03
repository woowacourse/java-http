package org.apache.coyote.http11.session;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.Request;

public class SessionManager implements Manager {

    private static final Map<String, HttpSession> SESSIONS = new HashMap<>();
    private static final SessionManager instance = new SessionManager();

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        return instance;
    }

    public static boolean loggedIn(Request request) {
        Optional<Cookies> cookie = request.getRequestHeaders().getCookie();
        if (cookie.isPresent()) {
            return checkSession(cookie.get());
        }
        return false;
    }

    private static boolean checkSession(Cookies cookies) {
        Optional<String> sessionCookie = cookies.getSessionCookie();
        if (sessionCookie.isPresent()) {
            String sessionId = sessionCookie.get();
            HttpSession session = SessionManager.getInstance().findSession(sessionId);
            return session != null;
        }
        return false;
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        if (SESSIONS.containsKey(id)) {
            return SESSIONS.get(id);
        }
        return null;
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}

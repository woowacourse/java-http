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
            HttpSession session = SESSIONS.get(sessionId);
            return session != null;
        }
        return false;
    }

    public static void enroll(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public void add(HttpSession session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public HttpSession findSession(String id) {
        return SESSIONS.get(id);
    }

    @Override
    public void remove(HttpSession session) {
        SESSIONS.remove(session.getId());
    }
}

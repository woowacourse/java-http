package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.Request;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

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
            Session session = SESSIONS.get(sessionId);
            return session != null;
        }
        return false;
    }

    public static void enroll(Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public void add(Session session) {
        SESSIONS.put(session.getId(), session);
    }
}

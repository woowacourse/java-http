package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.cookie.HttpCookie;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    public void updateSessionAndCookie(HttpRequest request, HttpResponse response) {
        final var sessionCookie = request.findCookie(Session.JSESSIONID);
        if (isValidSessionCookie(sessionCookie)) {
            request.setSession(SESSIONS.get(sessionCookie.getValue()));
            return;
        }
        final var session = Session.of();
        SESSIONS.put(session.getId(), session);
        request.setSession(session);
        response.addSetCookieHeader(Session.JSESSIONID, HttpCookie.ofSession(session));
    }

    private boolean isValidSessionCookie(HttpCookie sessionCookie) {
        if (sessionCookie == null) {
            return false;
        }
        final var sessionId = sessionCookie.getValue();
        final var savedSession = SESSIONS.get(sessionId);
        return savedSession != null;
    }
}

package org.apache.coyote.http11;

import java.util.UUID;

public class SessionHandler {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    public Session getSession(HttpRequest request, HttpResponse response) {
        String sessionId = request.getCookies().get(SESSION_COOKIE_NAME);
        if (sessionId != null) {
            Session session = SessionManager.getSession(sessionId);
            if (session != null) {
                return session;
            }
        }
        return createNewSession(response);
    }

    private Session createNewSession(HttpResponse response) {
        String newSessionId = UUID.randomUUID().toString();
        Session session = new Session(newSessionId);
        SessionManager.add(session);
        response.setCookie(SESSION_COOKIE_NAME, newSessionId);
        return session;
    }
}

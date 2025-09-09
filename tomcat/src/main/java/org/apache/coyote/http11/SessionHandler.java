package org.apache.coyote.http11;

import java.util.Optional;
import java.util.UUID;

public class SessionHandler {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    public Session getSession(HttpRequest request, HttpResponse response) {
        return Optional.ofNullable(request.getCookies().get(SESSION_COOKIE_NAME))
                .map(SessionManager::getSession)
                .orElseGet(() -> createNewSession(response));
    }

    private Session createNewSession(HttpResponse response) {
        String newSessionId = UUID.randomUUID().toString();
        Session session = new Session(newSessionId);
        SessionManager.add(session);
        response.setCookie(SESSION_COOKIE_NAME, newSessionId);
        return session;
    }
}

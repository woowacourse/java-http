package org.apache.catalina.session;

import java.util.Optional;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionParser {

    private static final String COOKIE_SESSION_KEY = "JSESSIONID";

    public static Optional<Session> extractCookieSessionFromRequest(final HttpRequest request) {
        final Optional<String> sessionId = request.getCookie().get(COOKIE_SESSION_KEY);
        if (sessionId.isEmpty()) {
            return Optional.empty();
        }
        final SessionManager sessionManager = SessionManager.getInstance();
        return Optional.ofNullable(sessionManager.findSession(sessionId.get()));
    }
}

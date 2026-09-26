package org.apache.catalina.session;

import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionResolver {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final SessionManager sessionManager;

    public SessionResolver(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Session resolve(final HttpRequest request) {
        final HttpCookie cookie = new HttpCookie(request.header("Cookie"));
        final String sessionId = cookie.get(SESSION_COOKIE_NAME);
        return sessionManager.findSession(sessionId);
    }
}

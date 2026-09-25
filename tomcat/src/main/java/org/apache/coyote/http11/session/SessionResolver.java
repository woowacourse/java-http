package org.apache.coyote.http11.session;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;

public class SessionResolver {

    private static final String SESSION_COOKIE_NAME = "JSESSIONID";

    private final SessionManager sessionManager;

    public SessionResolver(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public Session resolve(final HttpRequest request) {
        HttpCookie cookie = new HttpCookie(request.header("Cookie"));
        String sessionId = cookie.get(SESSION_COOKIE_NAME);
        return sessionManager.findSession(sessionId);
    }
}

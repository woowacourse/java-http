package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;

import java.util.UUID;

final class SessionSupport {

    private static final Manager SESSION_MANAGER = SessionManager.getInstance();

    private SessionSupport() {
    }

    static SessionContext from(final HttpRequest request) {
        final String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader != null) {
            final HttpCookie cookie = new HttpCookie(cookieHeader);
            final String sessionId = cookie.getJsessionId();
            if (sessionId != null) {
                return new SessionContext(sessionId, false);
            }
        }
        return new SessionContext(UUID.randomUUID().toString(), true);
    }

    static HttpSession find(final SessionContext context) throws java.io.IOException {
        return SESSION_MANAGER.findSession(context.id());
    }

    static HttpSession create(final SessionContext context) {
        final HttpSession session = new org.apache.catalina.Session(context.id());
        SESSION_MANAGER.add(session);
        return session;
    }

    static void addCookie(final HttpResponse response, final SessionContext context) {
        if (context.shouldSetCookie()) {
            response.setHeader("Set-Cookie", "JSESSIONID=" + context.id());
        }
    }

    record SessionContext(String id, boolean shouldSetCookie) {
    }
}

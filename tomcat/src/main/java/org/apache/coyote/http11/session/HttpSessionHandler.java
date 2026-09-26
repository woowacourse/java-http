package org.apache.coyote.http11.session;

import java.util.UUID;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HttpSessionHandler {

    private static final String SET_COOKIE = "Set-Cookie";

    private final SessionManager sessionManager;

    public HttpSessionHandler(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void prepare(final HttpRequest request) {
        request.setSessionManager(sessionManager);
    }

    public void writeSessionCookie(final HttpRequest request, final HttpResponse response) {
        request.getNewSession()
                .ifPresentOrElse(
                        session -> setSessionCookie(response, session.getId()),
                        () -> ensureSessionIdCookie(request, response)
                );
    }

    private void ensureSessionIdCookie(final HttpRequest request, final HttpResponse response) {
        if (request.getRequestedSessionId().isPresent()) {
            return;
        }

        setSessionCookie(response, UUID.randomUUID().toString());
    }

    private void setSessionCookie(final HttpResponse response, final String sessionId) {
        response.addHeader(SET_COOKIE, HttpCookie.SESSION_COOKIE_NAME + "=" + sessionId);
    }
}

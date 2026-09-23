package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SessionManager;

import java.util.Optional;
import java.util.UUID;

public class HttpSessionService {

    private static final String COOKIE_NAME = "JSESSIONID";

    private static final String SET_COOKIE = "Set-Cookie";

    private final SessionManager sessionManager;

    public HttpSessionService(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void ensureSessionIdCookie(final HttpRequest request, final HttpResponse response) {
        final Optional<String> existingSessionId = request.getCookie(COOKIE_NAME);

        if (existingSessionId.isPresent() && !existingSessionId.get().isBlank()) {
            return;
        }

        final String newSessionId = UUID.randomUUID().toString();

        setSessionCookie(response, newSessionId);
    }

    public HttpSession findSession(final HttpRequest request) {
        final Optional<String> sessionId = request.getCookie(COOKIE_NAME);

        if (sessionId.isEmpty() || sessionId.get().isBlank()) {

            return null;
        }

        return sessionManager.findSession(sessionId.get());
    }

    public HttpSession createSession(final HttpResponse response) {
        final HttpSession session = sessionManager.createSession();

        setSessionCookie(response, session.getId());

        return session;
    }

    public HttpSession replaceSession(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final HttpSession existingSession = findSession(request);

        if (existingSession != null) {
            existingSession.invalidate();
        }

        return createSession(response);
    }

    private void setSessionCookie(final HttpResponse response, final String sessionId) {
        response.addHeader(SET_COOKIE, COOKIE_NAME + "=" + sessionId);
    }
}
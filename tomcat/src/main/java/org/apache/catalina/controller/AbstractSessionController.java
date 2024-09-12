package org.apache.catalina.controller;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SessionService;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractSessionController extends AbstractController {

    private final SessionService sessionService;

    protected AbstractSessionController() {
        this(new SessionService());
    }

    protected AbstractSessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    protected HttpSession getSession(HttpRequest request) {
        String sessionId = request.getSessionId();
        return sessionService.findSession(sessionId)
                .orElseGet(() -> createRequestSession(request));
    }

    private HttpSession createRequestSession(HttpRequest request) {
        HttpSession createdSession = sessionService.createSession();
        request.addSession(createdSession.getId());
        return createdSession;
    }

    protected void saveSession(HttpSession session, HttpResponse response) {
        if (sessionService.isManagedSession(session)) {
            return;
        }
        HttpCookie sessionCookie = HttpCookie.ofSession(session.getId());
        response.addCookie(sessionCookie);
    }
}

package com.techcourse.service;

import com.techcourse.model.User;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;

public class SessionService {

    private static final String USER_SESSION_KEY = "USER";
    private static final String JSESSIONID = "JSESSIONID";

    public boolean isSessionValid(final Http11Request request) {
        final Manager manager = SessionManager.getInstance();
        if (request.isCookiesEmpty()) {
            return false;
        }

        final String sessionId = request.getJsessionid();
        if (sessionId == null) {
            return false;
        }

        final Session session = manager.findSession(sessionId);
        return session != null && session.getAttribute(USER_SESSION_KEY) != null;
    }

    public void createSession(final User user, final Http11Response response) {
        final Manager manager = SessionManager.getInstance();
        final Session session = new Session();
        session.setAttribute(USER_SESSION_KEY, user);
        manager.add(session);
        response.addCookie(JSESSIONID, session.getId());
    }
}

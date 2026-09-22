package com.techcourse;

import com.techcourse.model.User;
import org.apache.http.request.HttpRequest;
import org.qupring.session.Session;
import org.qupring.session.SessionManager;

public class LoginSessionService {

    private static final String SESSION_USER_KEY = "user";

    private final SessionManager sessionManager;

    public LoginSessionService() {
        this.sessionManager = SessionManager.getInstance();
    }

    public boolean isLoggedIn(HttpRequest request) {
        Session session = request.getSession(false);

        return session != null
                && session.getAttribute(SESSION_USER_KEY) != null;
    }

    public String login(HttpRequest request, User user) {
        Session session = request.getSession(true);
        session.setAttribute(SESSION_USER_KEY, user);
        sessionManager.add(session);

        return session.getId();
    }
}

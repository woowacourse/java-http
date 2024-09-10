package com.techcourse.controller;

import com.techcourse.exception.AuthenticationException;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.UUID;
import org.apache.coyote.http11.domain.controller.AbstractController;
import org.apache.coyote.http11.domain.cookie.Cookie;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.session.Session;
import org.apache.coyote.http11.domain.session.SessionManager;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (isAlreadyLogin(request)) {
            response.setRedirect("/index.html");
            return;
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");

        if (account == null || password == null) {
            response.setRedirect("/login.html");
            return;
        }

        try {
            User user = userService.login(account, password);

            String sessionId = UUID.randomUUID().toString();
            Cookie cookie = createSessionIdCookie(sessionId);
            createSession(sessionId, user);

            response.setRedirect("/index.html");
            response.setCookie(cookie);
        } catch (AuthenticationException e) {
            response.setRedirect("/401.html");
        }

    }

    private boolean isAlreadyLogin(HttpRequest request) {
        Session session = request.getSession();
        if (session != null) {
            User user = (User) session.getAttribute("user");
            return user != null;
        }
        return false;
    }

    private void createSession(String sessionId, User user) {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session(sessionId);
        sessionManager.add(session);
        session.setAttribute("user", user);
    }

    private Cookie createSessionIdCookie(String sessionId) {
        Cookie cookie = new Cookie();
        cookie.setValue("JSESSIONID", sessionId);
        return cookie;
    }
}

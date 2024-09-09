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
    protected HttpResponse doGet(HttpRequest request) {
        if (isAlreadyLogin(request)) {
            return HttpResponse.redirect("/index.html").build();
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");

        if (account == null || password == null) {
            return HttpResponse.redirect("/login.html").build();
        }

        try {
            User user = userService.login(account, password);

            String sessionId = UUID.randomUUID().toString();
            Cookie cookie = createSessionIdCookie(sessionId);
            createSession(sessionId, user);

            return HttpResponse.redirect("/index.html").setCookie(cookie).build();
        } catch (AuthenticationException e) {
            return HttpResponse.redirect("/401.html").build();
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

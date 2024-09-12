package com.techcourse.app.controller;

import com.techcourse.app.exception.AuthenticationException;
import com.techcourse.app.model.User;
import com.techcourse.app.service.UserService;
import com.techcourse.framework.handler.AbstractController;
import java.util.UUID;
import org.apache.coyote.http11.protocol.cookie.Cookie;
import org.apache.coyote.http11.protocol.request.HttpRequest;
import org.apache.coyote.http11.protocol.response.HttpResponse;
import org.apache.coyote.http11.protocol.session.Session;
import org.apache.coyote.http11.protocol.session.SessionManager;

public class LoginController extends AbstractController {

    public static final String SESSION_ID_KEY = "JSESSIONID";

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

        response.setRedirect("/login.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            response.setRedirect("/login.html");
            return;
        }
        login(response, account, password);
    }

    private boolean isAlreadyLogin(HttpRequest request) {
        String sessionId = getSessionIdFromCookie(request);
        if (sessionId == null) {
            return false;
        }
        return isUserLoggedIn(sessionId);
    }

    private String getSessionIdFromCookie(HttpRequest request) {
        Cookie cookie = request.getCookie();
        if (cookie == null) {
            return null;
        }
        return cookie.getValue(SESSION_ID_KEY);
    }

    private boolean isUserLoggedIn(String sessionId) {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(sessionId);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            return user != null;
        }
        return false;
    }

    private void login(HttpResponse response, String account, String password) {
        try {
            User user = userService.login(account, password);
            
            String sessionId = UUID.randomUUID().toString();
            createSession(sessionId, user);
            response.setRedirect("/index.html");
            response.setCookie(createSessionIdCookie(sessionId));
        } catch (AuthenticationException e) {
            response.setRedirect("/401.html");
        }
    }

    private void createSession(String sessionId, User user) {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session(sessionId);
        sessionManager.add(session);
        session.setAttribute("user", user);
    }

    private Cookie createSessionIdCookie(String sessionId) {
        Cookie cookie = new Cookie();
        cookie.setValue(SESSION_ID_KEY, sessionId);
        return cookie;
    }
}

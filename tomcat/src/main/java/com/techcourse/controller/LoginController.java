package com.techcourse.controller;

import com.techcourse.controller.dto.LoginRequest;
import com.techcourse.model.User;
import com.techcourse.service.UserService;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.util.StaticResourceReader;
import org.apache.coyote.http11.common.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseFile;

public class LoginController extends AbstractController {

    private static final ResponseFile loginPage = StaticResourceReader.read("/login.html");
    private static final String SESSION_KEY_USER = "user";

    private final SessionManager sessionManager;
    private final UserService userService;

    public LoginController() {
        this.sessionManager = SessionManager.getInstance();
        this.userService = UserService.getInstance();
    }

    @Override
    public String matchedPath() {
        return "/login";
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        sessionManager.getSession(request.getSessionId())
                .map(s -> s.getAttribute(SESSION_KEY_USER))
                .ifPresentOrElse(
                        user -> response.sendRedirect("/index.html"),
                        () -> response.setStatus(HttpStatusCode.OK).setBody(loginPage)
                );
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        LoginRequest loginRequest = LoginRequest.of(request.getBody());
        userService.login(loginRequest).ifPresentOrElse(
                user -> doLogin(request, response, user),
                () -> response.sendRedirect("/401.html")
        );
    }

    private void doLogin(HttpRequest request, HttpResponse response, User user) {
        Session session = sessionManager.getSession(request.getSessionId())
                .orElseGet(SessionManager::createNewSession);
        session.setAttribute(SESSION_KEY_USER, user);
        response.sendRedirect("/index.html")
                .setCookie("JSESSIONID", session.getId());
    }
}

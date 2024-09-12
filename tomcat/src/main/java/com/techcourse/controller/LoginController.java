package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.catalina.session.Session;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String USER_SESSION_NAME = "user";

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter(ACCOUNT);
        String password = request.getParameter(PASSWORD);

        userService.login(account, password)
                .ifPresentOrElse(
                        user -> {
                            Session session = request.getSession();
                            session.setAttribute(USER_SESSION_NAME, user);
                            response.addCookie(Cookies.ofJSessionId(session.getId()));
                            response.redirect("/index.html");
                        },
                        () -> response.redirect("/401.html")
                );
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        Session session = request.getSession();
        if (session.isEmpty()) {
            response.ok("login.html");
            return;
        }
        response.redirect("/index.html");
    }
}

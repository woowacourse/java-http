package com.techcourse.controller;

import com.techcourse.service.UserService;
import java.io.IOException;
import org.apache.catalina.Session;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.Cookies;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final UserService userService = UserService.getInstance();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        userService.login(account, password)
                .ifPresentOrElse(
                        user -> {
                            Session session = request.getSession();
                            session.setAttribute("user", user);
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

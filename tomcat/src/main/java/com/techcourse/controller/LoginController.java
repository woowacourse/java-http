package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.resource.StaticResource;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private static final String LOGIN_PAGE = "/login.html";

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request.getSession())) {
            response.redirect("/index.html");
            return;
        }
        StaticResource.from(LOGIN_PAGE).writeTo(response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<User> user = userService.login(request.getParameter("account"), request.getParameter("password"));
        if (user.isEmpty()) {
            response.redirect("/401.html");
            return;
        }

        request.getSession().setAttribute("user", user.get());
        response.redirect("/index.html");
    }

    private boolean isLoggedIn(Session session) {
        return session.getAttribute("user") != null;
    }
}

package com.techcourse.controller;

import com.techcourse.model.User;
import com.techcourse.service.UserService;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (isLoggedIn(request.getSession())) {
            response.sendRedirect("/index.html");
            return;
        }
        response.setStaticResource(request.getPath());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<User> user = userService.login(request.getParameter("account"), request.getParameter("password"));
        if (user.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        request.getSession().setAttribute("user", user.get());
        response.sendRedirect("/index.html");
    }

    private boolean isLoggedIn(Session session) {
        return session.getAttribute("user") != null;
    }
}

package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.view.ResourceRenderer;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class LoginController extends AbstractController {
    private final ResourceRenderer resourceRenderer;

    public LoginController(ResourceRenderer resourceRenderer) {
        this.resourceRenderer = resourceRenderer;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        resourceRenderer.render("/login.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getSession().getAttribute("user") != null || request.getBody().isEmpty()) {
            doGet(request, response);
            return;
        }

        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            response.sendRedirect("/401.html");
            return;
        }

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        if (foundUser.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        request.getSession().setAttribute("user", foundUser.get());
        response.sendRedirect("/index.html");
    }
}

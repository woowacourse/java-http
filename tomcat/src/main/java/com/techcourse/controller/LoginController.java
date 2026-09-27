package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Optional;
import org.apache.catalina.AbstractController;
import org.apache.catalina.StaticResourceHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class LoginController extends AbstractController {

    private final StaticResourceHandler resourceHandler;

    public LoginController(StaticResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        resourceHandler.handle("/login.html", HttpStatus.OK, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Optional<User> user = findAuthenticatedUser(
                request.getFormParameter("account"), request.getFormParameter("password"));
        if (user.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        request.getSession().setAttribute("user", user.get());
        response.sendRedirect("/index.html");
    }

    private Optional<User> findAuthenticatedUser(String account, String password) {
        if (account == null || account.isBlank() || password == null || password.isBlank()) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}

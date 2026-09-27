package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.AbstractController;
import org.apache.catalina.StaticResourceHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

public class RegisterController extends AbstractController {

    private final StaticResourceHandler resourceHandler;

    public RegisterController(StaticResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }

        resourceHandler.handle("/register.html", HttpStatus.OK, response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        String account = request.getFormParameter("account");
        String password = request.getFormParameter("password");
        String email = request.getFormParameter("email");
        if (account == null || password == null || email == null) {
            resourceHandler.handle("/register.html", HttpStatus.OK, response);
            return;
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.sendRedirect("/index.html");
    }
}

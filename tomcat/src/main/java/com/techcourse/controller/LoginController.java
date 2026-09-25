package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.controller.AbstractController;
import org.apache.catalina.resource.ResourceHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;

public class LoginController extends AbstractController {

    private final ResourceHandler resources;

    public LoginController(final ResourceHandler resources) {
        this.resources = resources;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        if (request.getSession().getAttribute("user") != null) {
            response.sendRedirect("/index.html");
            return;
        }
        resources.serve("/login.html", response);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        if (account == null || password == null) {
            response.sendRedirect("/401.html");
            return;
        }
        final var user = InMemoryUserRepository.findByAccount(account)
                .filter(candidate -> candidate.checkPassword(password));
        user.ifPresent(value -> request.getSession().setAttribute("user", value));
        response.sendRedirect(user.isPresent() ? "/index.html" : "/401.html");
    }
}

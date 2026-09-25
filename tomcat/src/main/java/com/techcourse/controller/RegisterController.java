package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {
    private final StaticResourceController resources = new StaticResourceController();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws IOException {
        resources.render("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if (account == null || password == null || email == null) {
            doGet(request, response);
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }
}

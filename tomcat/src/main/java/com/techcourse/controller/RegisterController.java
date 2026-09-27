package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            response.setStatus(400, "Bad Request");
            response.setBody("Account, password and email are required.", "text/plain");
            return;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }
}

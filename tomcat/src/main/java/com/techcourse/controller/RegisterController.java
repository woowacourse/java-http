package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request, Session session) throws IOException {
        return resourceResponse(request, session, "static/register.html");
    }

    @Override
    protected HttpResponse doPost(HttpRequest request, Session session) throws IOException {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");

        if (!validateInput(account) || !validateInput(password) || !validateInput(email)) {
            return redirectResponse(request, session, "/register.html");
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return redirectResponse(request, session, "/login.html");
    }

    private boolean validateInput(String input) {
        return input != null && !input.isBlank();
    }
}

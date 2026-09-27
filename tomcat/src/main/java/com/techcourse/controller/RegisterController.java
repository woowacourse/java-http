package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatusCode;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends AbstractController {

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final boolean isRegistered  = registerNewUser(request.getParseBodyQuery());
        if (isRegistered) {
            response.sendRedirectResponse("/index.html");
            return;
        }
        response.sendForwardResponse(HttpStatusCode.BAD_REQUEST, "/register.html");
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.sendForwardResponse(HttpStatusCode.OK, "/register.html");
    }

    private boolean registerNewUser(final Map<String, String> registerInfoPairs) {
        String account = registerInfoPairs.getOrDefault("account", "");
        String password = registerInfoPairs.getOrDefault("password", "");
        String email = registerInfoPairs.getOrDefault("email", "");

        if (!account.isBlank() && !password.isBlank() && !email.isBlank()) {
            final User newUser = new User(account, password, email);
            InMemoryUserRepository.save(newUser);
            return true;
        }

        return false;
    }
}

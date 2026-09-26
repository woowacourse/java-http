package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class RegisterController extends AbstractController {

    public RegisterController() {
        super("GET", "POST");
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        response.forward("/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws IOException {
        final Map<String, String> params = request.getBodyParams();
        final User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email")
        );
        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }
}

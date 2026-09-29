package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceController;

public class RegisterController extends AbstractController {

    private final StaticResourceController staticResources;

    public RegisterController(final StaticResourceController staticResources) {
        this.staticResources = staticResources;
    }

    @Override
    protected void doGet(
            final HttpRequest request,
            final HttpResponse response
    ) throws Exception {
        staticResources.render("/register.html", response);
    }

    @Override
    protected void doPost(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final Map<String, String> parameters = request.getFormParameters();
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        final String email = parameters.get("email");

        final String location;

        if (account == null || account.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            location = "/register";
        } else {
            InMemoryUserRepository.save(new User(account, password, email));
            location = "/index.html";
        }

        response.setStatus(302, "Found");
        response.addHeader("Location", location);
    }
}

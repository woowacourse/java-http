package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        getSession(request, response);
        renderResource(response, "/register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        getSession(request, response);
        final Map<String, String> formData = request.getParameters();
        final String account = formData.get("account");
        final String password = formData.get("password");
        final String email = formData.get("email");

        if (account != null && password != null && email != null) {
            InMemoryUserRepository.save(new User(account, password, email));
        }

        redirect(response, "/index.html");
    }
}

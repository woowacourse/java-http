package com.techcourse.web.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.ResourceLoader;
import java.util.Optional;
import org.apache.coyote.AbstractController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestParameters;

public class RegisterController extends AbstractController {

    private static final String REGISTER_PAGE = "/register.html";
    private static final String INDEX_PAGE = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        Optional<String> body = ResourceLoader.read(REGISTER_PAGE);
        if (body.isEmpty()) {
            renderNotFound(response);
            return;
        }
        response.ok(ResourceLoader.contentTypeOf(REGISTER_PAGE), body.get());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        RequestParameters formData = request.getFormData();
        Optional<String> account = formData.get("account");
        Optional<String> password = formData.get("password");
        Optional<String> email = formData.get("email");

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            response.sendRedirect(REGISTER_PAGE);
            return;
        }

        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        response.sendRedirect(INDEX_PAGE);
    }
}

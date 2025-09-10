package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.controller.AbstractController;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public boolean isProvide(final String path) {
        return "/register".equals(path);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) {
        response.setOk("register.html");
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        final Map<String, String> bodyElement = request.getBodyElement();

        final String account = bodyElement.get("account");
        final String email = bodyElement.get("email");
        final String password = bodyElement.get("password");

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        response.setFound("index.html");
    }
}

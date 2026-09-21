package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.util.Optional;
import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.StaticResourceBody;

public class RegisterController extends AbstractController {

    @Override
    protected HttpResponse doGet(HttpRequest request) throws IOException {
        return HttpResponse.ok(StaticResourceBody.from("/register.html"));
    }

    @Override
    protected HttpResponse doPost(HttpRequest request) {
        RequestBody body = request.body();

        Optional<String> account = body.get("account");
        Optional<String> password = body.get("password");
        Optional<String> email = body.get("email");

        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return HttpResponse.redirect("/register");
        }

        if (InMemoryUserRepository.findByAccount(account.get()).isPresent()) {
            return HttpResponse.redirect("/register");
        }

        User user = new User(account.get(), password.get(), email.get());
        InMemoryUserRepository.save(user);

        return HttpResponse.redirect("/index.html");
    }
}

package org.apache.coyote.http11.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(
            HttpRequest request,
            HttpResponse response
    ) throws IOException {
        response.fromResource("register.html");
    }

    @Override
    protected void doPost(
            HttpRequest request,
            HttpResponse response
    ) {
        String account = request.getBodyValue("account");
        String email = request.getBodyValue("email");
        String password = request.getBodyValue("password");

        if (account == null || email == null || password == null) {
            return;
        }

        InMemoryUserRepository.save(
                new User(account, password, email)
        );

        response.addHeader("Location", "/index.html");
    }
}

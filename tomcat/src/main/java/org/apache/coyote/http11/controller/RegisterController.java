package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.util.Optional;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String[] splitRequestBody = request.getBody().split("&");
        Optional<String> account = getValueOf("account", splitRequestBody);
        Optional<String> email = getValueOf("email", splitRequestBody);
        Optional<String> password = getValueOf("password", splitRequestBody);

        if (account.isEmpty() || email.isEmpty() || password.isEmpty()) {
            response.setHttpStatus(HttpStatus.BAD_REQUEST).setResponseFileName("/register.html");
            return;
        }

        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        response.setHttpStatus(HttpStatus.FOUND).setResponseFileName("/index.html");
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.setHttpStatus(HttpStatus.OK).setResponseFileName("/register.html");
    }
}

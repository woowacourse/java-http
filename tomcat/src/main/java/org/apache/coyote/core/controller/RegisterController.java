package org.apache.coyote.core.controller;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws UncheckedServletException {
        super.doPost(request, response);
        registerUser(request);
    }

    private void registerUser(final HttpRequest request) {
        Map<String, String> requestBodies = request.getRequestBodies();
        String account = requestBodies.get("account");
        String password = requestBodies.get("password");
        String email = requestBodies.get("email");

        validateUserInformation(account, password, email);

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private void validateUserInformation(final String account, final String password, final String email) {
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException();
        }
    }
}

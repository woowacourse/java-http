package com.techcourse.controller;

import static com.techcourse.controller.RequestPath.INDEX;
import static com.techcourse.controller.RequestPath.REGISTER;

import org.apache.coyote.http.HttpQueryParams;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.sendRedirect(REGISTER.path());
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        register(request);
        response.sendRedirect(INDEX.path());
    }

    private void register(final HttpRequest request) {
        HttpQueryParams queryParams =  request.getQueryParamsFromBody();
        User user = new User(
                queryParams.get(ACCOUNT),
                queryParams.get(PASSWORD),
                queryParams.get(EMAIL));
        InMemoryUserRepository.save(user);
    }
}

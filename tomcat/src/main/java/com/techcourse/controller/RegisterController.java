package com.techcourse.controller;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.QueryParameters;
import org.apache.coyote.http11.response.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.redirect(request.getVersion(), "/register.html");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        User user = createUser(request.getQueryParameters());
        InMemoryUserRepository.save(user);
        response.redirect(request.getVersion(), "/index.html");
    }

    private User createUser(QueryParameters queryParameters) {
        return new User(queryParameters.get("account"), queryParameters.get("password"), queryParameters.get("email"));
    }
}

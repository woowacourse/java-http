package com.techcourse.controller;

import org.apache.coyote.http.HttpQueryParams;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private static final String RESOURCE_PATH = "/register.html";
    private static final String ROOT_RESOURCE_PATH = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.sendRedirect(RESOURCE_PATH);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        register(request);
        response.sendRedirect(ROOT_RESOURCE_PATH);
    }

    private void register(final HttpRequest request) {
        HttpQueryParams queryParams =  request.getQueryParamsFromBody();
        User user = new User(
                queryParams.get("account"),
                queryParams.get("password"),
                queryParams.get("email"));
        InMemoryUserRepository.save(user);
    }
}

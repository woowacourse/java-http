package com.techcourse.controller;

import org.apache.coyote.http.HttpBody;
import org.apache.coyote.http.HttpQueryParams;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

public class RegisterController extends AbstractController {

    private static final String RESOURCE_PATH = "/register.html";
    private static final String ROOT_RESOURCE_PATH = "/index.html";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.sendRedirect(RESOURCE_PATH);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        register(request);
        response.sendRedirect(ROOT_RESOURCE_PATH);
    }

    private void register(final HttpRequest request) {
        HttpBody body = request.getBody();
        String content = body.getContent();
        HttpQueryParams queryParams = new HttpQueryParams(content);
        User user = new User(queryParams.get("account"), queryParams.get("password"), queryParams.get("email"));
        InMemoryUserRepository.save(user);
    }
}

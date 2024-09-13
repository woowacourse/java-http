package com.techcourse.controller;

import java.util.Map;

import org.apache.catalina.controller.AbstractController;
import org.apache.coyote.http11.request.HttpRequest;
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
        User user = createUser(request.getRequestBody());
        InMemoryUserRepository.save(user);
        response.redirect(request.getVersion(), "/index.html");
    }

    private User createUser(Map<String, String> requestBody) {
        return new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
    }
}

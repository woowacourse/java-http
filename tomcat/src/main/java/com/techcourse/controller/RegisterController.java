package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public class RegisterController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        User user = new User(request.getParameters("account"), request.getParameters("password"),
                request.getParameters("email"));

        InMemoryUserRepository.save(user);
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String body = resourceReader.read("/register.html");
        response.setBody(body);
    }
}

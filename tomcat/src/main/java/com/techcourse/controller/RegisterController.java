package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StaticResourceHandler;

public class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        StaticResourceHandler.handleStaticResource("/register.html", response);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        String account = request.getQueryParams().get("account");
        String password = request.getQueryParams().get("password");
        String email = request.getQueryParams().get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        response.setStatusLine("HTTP/1.1", 302, "Found");
        response.addHeader("Location", "/index.html");
    }
}

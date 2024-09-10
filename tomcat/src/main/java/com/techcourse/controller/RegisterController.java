package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final Http11ResourceFinder resourceFinder = new Http11ResourceFinder();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        if (isLogin(request)) {
            response.setRedirect("/index.html");
            return;
        }

        Path path = resourceFinder.find(request.requestUri());
        response.setBodyAndContentType(path);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        if (isLogin(request)) {
            response.setRedirect("/index.html");
            return;
        }

        LinkedHashMap<String, String> requestBody = request.body();
        String account = requestBody.getOrDefault("account", "");
        String password = requestBody.getOrDefault("password", "");
        String email = requestBody.getOrDefault("email", "");

        InMemoryUserRepository.save(new User(account, password, email));

        response.setRedirect("/index.html");
    }
}

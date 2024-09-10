package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.apache.coyote.http11.AbstractController;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final Http11ResourceFinder resourceFinder = new Http11ResourceFinder();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Path path = resourceFinder.find(request.requestUri());
        response.setBodyAndContentType(path);
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        LinkedHashMap<String, String> requestBody = request.body();
        String account = requestBody.getOrDefault("account", "");
        String password = requestBody.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        boolean loginSuccess = user.isPresent() && user.get().checkPassword(password);
        if (loginSuccess) {
            response.setRedirect("/index.html");
        }
    }
}

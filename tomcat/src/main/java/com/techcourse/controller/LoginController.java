package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Optional;
import org.apache.coyote.http11.Http11Cookie;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final Http11ResourceFinder resourceFinder = new Http11ResourceFinder();

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        boolean alreadyLogin = request.findSessionCookie().map(Http11Cookie::value).map(SESSION_MANAGER::findSession)
                .map(session -> session.getAttribute("user"))
                .isPresent();
        if (alreadyLogin) {
            response.setRedirect("/index.html");
            return;
        }
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
            request.findSessionCookie()
                    .map(Http11Cookie::value)
                    .map(SESSION_MANAGER::findSession)
                    .ifPresent(session -> session.setAttribute("user", user));
            response.setRedirect("/index.html");
        }
    }
}

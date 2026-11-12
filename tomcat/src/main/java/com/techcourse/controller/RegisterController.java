package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.login.LoginParser;

public final class RegisterController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        String resourceType = response.resourceType();
        response.setResponse(HttpResponse.ok(StaticResourceLoader.load("/register.html"), resourceType));
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) {
        Map<String, String> body = parseRequestBody(request);
        if (!registerUser(body.get("account"), body.get("password"), body.get("email"))) {
            response.setResponse(HttpResponse.redirect("/401.html"));
            return;
        }
        response.setResponse(HttpResponse.redirect("/index.html"));
    }

    private Map<String, String> parseRequestBody(HttpRequest request) {
        boolean isFormUrlEncoded = request.header("Content-Type")
            .map(value -> value.startsWith("application/x-www-form-urlencoded"))
            .orElse(false);
        return isFormUrlEncoded ? LoginParser.parseQueryString(request.body()) : Map.of();
    }

    private boolean registerUser(String account, String password, String email) {
        if (account == null || password == null || email == null) {
            return false;
        }
        try {
            InMemoryUserRepository.save(new User(account, password, email));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

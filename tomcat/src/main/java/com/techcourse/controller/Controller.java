package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ResponseCreator;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class Controller {
    private final ResponseCreator responseCreator;

    public Controller() {
        this.responseCreator = new ResponseCreator();
    }

    public String getHelloWorldPage(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getPath());
    }

    public String getDefaultPage(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getPath());
    }

    public String getLoginPage(HttpRequest httpRequest) throws IOException {
        return responseCreator.create(200, httpRequest.getPath());
    }

    public String login(HttpRequest httpRequest) throws IOException {
        Map<String, String> params = httpRequest.getPayload();
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(params.get("account"));
        if (optionalUser.isEmpty()) {
            return responseCreator.create(401, "/401.html");
        }
        User user = optionalUser.get();
        if (user.checkPassword(params.get("password"))) {
            System.out.println(user);
            return responseCreator.create(302, "/index.html");
        }
        return responseCreator.create(401, "/401.html"); //todo 리다이렉트
    }
}

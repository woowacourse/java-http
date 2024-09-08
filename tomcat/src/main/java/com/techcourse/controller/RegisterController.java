package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.ResponseCreator;

import java.io.IOException;
import java.util.Map;

public class RegisterController implements Controller {

    private final ResponseCreator responseCreator;

    public RegisterController() {
        this.responseCreator = new ResponseCreator();
    }

    @Override
    public String execute(HttpRequest httpRequest) throws IOException {
        if (httpRequest.getHttpMethod() == HttpMethod.GET) {
            return responseCreator.create(200, httpRequest.getUrl());
        }
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            Map<String, String> payload = httpRequest.getBody();
            User user = new User(payload.get("account"), payload.get("password"), payload.get("email"));
            InMemoryUserRepository.save(user);

            return responseCreator.create(200, "/index.html");
        }

        return responseCreator.create(400, "유효하지 않은 요청입니다.");
    }
}

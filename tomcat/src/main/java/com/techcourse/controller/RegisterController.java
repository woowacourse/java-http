package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.request.HttpMethod;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;

import java.util.Map;

public class RegisterController implements Controller {

    @Override
    public HttpResponse execute(HttpRequest httpRequest) {
        if (httpRequest.getHttpMethod() == HttpMethod.GET) {
            return new HttpResponse(200, httpRequest.getUrl());
        }
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            Map<String, String> payload = httpRequest.getBody();
            User user = new User(payload.get("account"), payload.get("password"), payload.get("email"));
            InMemoryUserRepository.save(user);

            return new HttpResponse(302, "/index.html");
        }

        return new HttpResponse(400);
    }
}
